import os
import shutil
import subprocess

from collections import OrderedDict
from datetime import datetime, timedelta


#Adjust as desired
working_base = "/tmp/pkix-test-data"
openssl_config="openssl.cnf"
key_length=3072
signing_digest = "sha256"
cert_days = 365*10*3
ca_exts="ca_exts"
end_entity_exts="end_entity_exts"
altname_exts="altname_req"
crl_days = cert_days
crl_v2_exts="crl_ext"

now_utc = datetime.utcnow()

ca_config = {
    "root1-ca" :   {
        "subs" : {
            "inter1A-ca":  { 
                "subs": {
                    "inter1A1-ca": { },
                }
            },
            "inter1B-ca":  { },
        }
    },

    "root2-ca" : {
        "subs" : {
            "inter2A-ca":  { },
            "inter2B-ca":  { },
        }
    },

    "root3-ca" :   {
    },

    "mdt-root" : {
        "subj": "CN=Snakeoil Root CA",
        "subs": {
            "mdt-ica.1":  {
                "subj": "CN=Snakeoil Metadata Issuing CA #1",
                "exts": ["any_policy_exts"],
            },
            "mdt-ica.2":  {
                "subj": "CN=Snakeoil Metadata Issuing CA #2",
                "exts": ["1_policy_exts"],
            },
            "mdt-ica.3":  {
                "subj": "CN=Snakeoil Metadata Issuing CA #3",
                "exts": ["2_policy_exts"],
            },
        }
    }

}

ee_config = (
    # This one is the standard, normal, valid cert
    { "name": "foo-1A1-good", "san": "DNS:foo.example.org,URI:https://foo.example.org/sp", "cn" : "/CN=foo.example.org", "signer" : "inter1A1-ca", "days": cert_days, "exts": altname_exts},

    # This one is expired, only has validity duration of 10 seconds
    { "name": "foo-1A1-expired", "san": "DNS:foo.example.org,URI:https://foo.example.org/sp", "cn": "/CN=foo.example.org", "signer": "inter1A1-ca", "exts": altname_exts,
        "days": None,  
        "startdate": now_utc,
        "enddate": now_utc + timedelta(seconds=10) },

    # This one will be revoked, and will appear in generated CRLs
    { "name": "foo-1A1-revoked", "san": "DNS:foo.example.org,URI:https://foo.example.org/sp", "cn": "/CN=foo.example.org", "signer": "inter1A1-ca", "days": cert_days, "exts": altname_exts},

    # These are for the cert policy stuff
    # NOTE: can only have 1 -reqexts arg apparently, as of openssl 1.0.2. So don't worry about altnames on these for now.  The original ones didn't have alt names anyway.
    # When we can assume at least openssl 1.1.1, can use the new -addexts to add extensions directly on the command-line, rather than indirectly via config file.
    { "name": "mdt-signer.1", "cn": "/CN=Snakeoil Metadata Signer #1", "signer": "mdt-ica.1", "days": cert_days, "exts": "1_policy_exts"},
    { "name": "mdt-signer.2", "cn": "/CN=Snakeoil Metadata Signer #2", "signer": "mdt-ica.2", "days": cert_days, "exts": "1_policy_exts"},
    { "name": "mdt-signer.3", "cn": "/CN=Snakeoil Metadata Signer #3", "signer": "mdt-ica.3", "days": cert_days, "exts": "1_policy_exts"},
)


# Start main()
def main():

    # Initialize all CAs
    for ca, config in ca_config.items():
        init_ca(ca, config, None)

    # Issue end entity certs from CAs
    for ee_data in ee_config:
        ee_name=ee_data['name']
        ee_keypath=os.path.join(os.getcwd(), ee_name+".key")
        ee_csrpath=os.path.join("/tmp", ee_name+".csr")
        ee_certpath=os.path.join(os.getcwd(), ee_name+".crt")
        ee_exts = ee_data.get('exts', None)
        ee_altnames = ee_data.get('san', None)

        duration_args = {}
        if "days" in ee_data and ee_data["days"]:
            duration_args["days"] = ee_data["days"]
        elif "startdate" in ee_data and "enddate" in ee_data:
            # the datetime instances should already have been specified in UTC, e.g. via datetime.utcnow()
            duration_args["days"] = None
            duration_args["startdate"] = ee_data["startdate"].strftime("%y%m%d%H%M%S") + "Z"
            duration_args["enddate"] = ee_data["enddate"].strftime("%y%m%d%H%M%S") + "Z"

        gen_key(ee_keypath)

        try:
            if ee_altnames:
                os.environ['SAN'] = ee_data['san']
            gen_csr(ee_keypath, ee_csrpath, ee_data['cn'], exts=ee_exts)
        finally:
            if ee_altnames:
                os.environ.pop('SAN')

        sign_csr(ee_csrpath, ee_certpath, build_ca_dirpath(ee_data['signer']), end_entity_exts, **duration_args)

    # Initial empty CRL
    gen_crl(build_ca_dirpath("inter1A1-ca"), os.path.join(os.getcwd(), "inter1A1-v1-empty.crl"))

    # Revoke "foo-1A1-revoked"
    revoke_cert(build_ca_dirpath("inter1A1-ca"), os.path.join(os.getcwd(), "foo-1A1-revoked.crt"))

    # Valid V1 CRL
    gen_crl(build_ca_dirpath("inter1A1-ca"), os.path.join(os.getcwd(), "inter1A1-v1.crl"))

    # Valid V2 CRL
    gen_crl(build_ca_dirpath("inter1A1-ca"), os.path.join(os.getcwd(), "inter1A1-v2.crl"), exts=crl_v2_exts)

    # Expired V1 CRL (well, will expire in 1 hour)
    gen_crl(build_ca_dirpath("inter1A1-ca"), os.path.join(os.getcwd(), "inter1A1-v1-expired.crl"), days=None, hours=1)


# End main()


##############################
### Utility functions here ###
##############################

def revoke_cert(cahome, cert_to_revoke):
    params = {
        "-revoke": cert_to_revoke,
    }

    try:
        os.environ['CAHOME'] = cahome
        execute_openssl("ca", params)
    finally:
        os.environ.pop('CAHOME')

def gen_crl(cahome, crlout, days=crl_days, hours=None, digest=signing_digest, exts=None):
    # Using this because order matters, "-gencrl" must be first
    params = OrderedDict()
    params["-gencrl"] = None
    params.update({
        "-out": crlout,
        "-md": digest,
    })
    if days:
        params["-crldays"] = str(days)
    if hours:
        params["-crlhours"] = str(hours)
    if exts:
        params["-crlexts"] = str(exts)

    try:
        os.environ['CAHOME'] = cahome
        execute_openssl("ca", params)
    finally:
        os.environ.pop('CAHOME')

def sign_csr(csr, certout, cahome, exts, days=cert_days, startdate=None, enddate=None, digest=signing_digest):
    params = {
        "-in": csr,
        "-out": certout,
        "-md": digest,
        "-extensions": exts,
    }
    if days:
        params["-days"] = str(days)
    if startdate:
        params["-startdate"] = startdate
    if enddate:
        params["-enddate"] = enddate

    try:
        os.environ['CAHOME'] = cahome
        execute_openssl("ca", params)
    finally:
        os.environ.pop('CAHOME')

def gen_csr(key, csrout, subject, exts=None, digest=signing_digest):
    params = {
        "-new": None,
        "-key": key,
        "-out": csrout,
        "-subj": subject,
        "-"+digest: None,
    }
    if exts:
        params['-reqexts'] = exts
    execute_openssl("req", params)

def gen_key(path, length=key_length):
    # Using this because order matters here - the 'length' param must be last
    params = OrderedDict({
        "-out": path,
    })
    params[str(length)] = None
    execute_openssl("genrsa", params, include_config=False)

def execute_openssl(cmd, params, include_config=True):
    to_execute = ["openssl", cmd]
    if include_config:
        to_execute.extend(["-config", openssl_config])
    for param,value in params.items():
        if value is None:
            to_execute.append(param)
        elif type(value) is list:
            for member in value:
                to_execute.extend([param, member])
        else:
            to_execute.extend([param, value])
    print("to_execute", to_execute)
    subprocess.run(to_execute)

def init_new_ca_dir(rootdir):
    if os.path.exists(rootdir):
        shutil.rmtree(rootdir)
    os.makedirs(rootdir)
    os.mkdir(os.path.join(rootdir, "newcerts"))
    open(os.path.join(rootdir, "index.txt"), "x").close()
    with open(os.path.join(rootdir, "serial"), "x") as serial:
        print("01", file=serial)

def build_ca_dirpath(ca, base=working_base):
    return os.path.join(base, ca)

def init_ca(ca, config, parent):
    rootdir = build_ca_dirpath(ca)
    print("Initializing CA {} in root dir {}".format(ca, rootdir))
    init_new_ca_dir(rootdir)

    keypath = os.path.join(rootdir, "ca.key")
    csrpath = os.path.join(rootdir, "ca.csr")
    certpath = os.path.join(rootdir, "ca.crt")

    subject= "/" + config.get("subj", "CN="+ca)

    gen_key(keypath)

    if parent:
        # It's not a root CA, so generate a CSR and sign with the parent CA
        gen_csr(keypath, csrpath, subject, exts=config.get("exts", None))
        sign_csr(csrpath, certpath, build_ca_dirpath(parent), ca_exts) 
    else:
        # Create self-signed certs for all roots
        params = {
            "-new": None,
            "-x509": None,
            "-key": keypath,
            "-out": certpath,
            "-days": str(cert_days),
            "-subj": subject,
            "-"+signing_digest: None,
            "-extensions": ca_exts,
        }
        execute_openssl("req", params)

    # Copy the new CA key and cert out to the current working directory
    shutil.copy(keypath, os.path.join(os.getcwd(), ca+".key"))
    shutil.copy(certpath, os.path.join(os.getcwd(), ca+".crt"))

    if "subs" in config:
        for sub, subconfig in config["subs"].items():
            init_ca(sub, subconfig, ca)


if __name__ == "__main__":
    main()


