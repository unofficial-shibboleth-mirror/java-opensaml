/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.profile.action;

import javax.annotation.Nonnull;

import org.opensaml.profile.context.ProfileRequestContext;

import org.testng.Assert;
import org.testng.annotations.Test;

/** Unit test for {@link AbstractProfileAction}. */
public class AbstractProfileActionTest {


    /**
     * Test a successful action run.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testSuccess() throws Exception {

        BaseProfileAction action = new BaseProfileAction();
        action.initialize();
        action.execute(new ProfileRequestContext());

        Assert.assertTrue(action.didPre);
        Assert.assertTrue(action.didExec);
        Assert.assertTrue(action.didPost);
    }

    /**
     * Test a failure in the preexec step.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testPreFailure() throws Exception {

        BaseProfileAction action = new PreFailProfileAction();
        action.initialize();

        try {
            action.execute(new ProfileRequestContext());
        } catch (Exception e) {
            Assert.assertFalse(action.didPre);
            Assert.assertFalse(action.didExec);
            Assert.assertFalse(action.didPost);
        }
    }

    /**
     * Test a failure in the exec step.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testExecFailure() throws Exception {

        BaseProfileAction action = new ExecFailProfileAction();
        action.initialize();

        try {
            action.execute(new ProfileRequestContext());
        } catch (NullPointerException e) {
            Assert.assertTrue(e.getSuppressed()[0] instanceof RuntimeException);
            Assert.assertTrue(action.didPre);
            Assert.assertFalse(action.didExec);
            Assert.assertFalse(action.didPost);
        }
    }

    /**
     * Test an unchecked error in the exec step.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testExecUnchecked() throws Exception {

        BaseProfileAction action = new ExecUncheckedProfileAction();
        action.initialize();

        try {
            action.execute(new ProfileRequestContext());
        } catch (NullPointerException e) {
            Assert.assertTrue(e.getSuppressed()[0] instanceof IllegalArgumentException);
            Assert.assertTrue(action.didPre);
            Assert.assertFalse(action.didExec);
            Assert.assertTrue(action.didPost);
        }
    }

    /**
     * Test a failure in the post step.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testPostFailure() throws Exception {

        BaseProfileAction action = new PostFailProfileAction();
        action.initialize();

        try {
            action.execute(new ProfileRequestContext());
        } catch (NullPointerException e) {
            Assert.assertTrue(action.didPre);
            Assert.assertTrue(action.didExec);
            Assert.assertFalse(action.didPost);
        }
    }

    private class BaseProfileAction extends AbstractProfileAction {
        private boolean didPre = false;
        private boolean didExec = false;
        private boolean didPost = false;
        
        protected boolean doPreExecute(@Nonnull final ProfileRequestContext prc) {
            return didPre = true;
        }
        
        protected void doExecute(@Nonnull final ProfileRequestContext prc) {
            didExec = true;
        }

        protected void doPostExecute(@Nonnull final ProfileRequestContext prc) {
            didPost = true; 
        }
    }

    private class PreFailProfileAction extends BaseProfileAction {
        
        protected boolean doPreExecute(@Nonnull final ProfileRequestContext prc) {
            throw new RuntimeException();
        }
    }
    
    private class ExecFailProfileAction extends BaseProfileAction {
        
        protected void doExecute(@Nonnull final ProfileRequestContext prc) {
            throw new RuntimeException();
        }

        protected void doPostExecute(@Nonnull final ProfileRequestContext prc) {
            throw new NullPointerException();
        }
    }

    private class ExecUncheckedProfileAction extends BaseProfileAction {
        
        protected void doExecute(@Nonnull final ProfileRequestContext prc) {
            throw new IllegalArgumentException();
        }

        protected void doPostExecute(@Nonnull final ProfileRequestContext prc) {
            super.doPostExecute(prc);
            throw new NullPointerException();
        }
    }

    private class PostFailProfileAction extends BaseProfileAction {
        
        protected void doPostExecute(@Nonnull final ProfileRequestContext prc) {
            throw new NullPointerException();
        }
    }

}
