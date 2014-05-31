/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui;

/**
 *
 * @author bennyl
 */
@RegisterController("test.dummy")
public class TestDummyController extends Controller {

    int onLoadViewCalls = 0;

    @Override
    public Object getView() {
        return null;
    }

    @Override
    protected void onLoadView() {
        onLoadViewCalls++;
    }

    public int getOnLoadViewCalls() {
        return onLoadViewCalls;
    }

}
