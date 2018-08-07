package com.zhxh.modulebase;

import com.zhxh.modulebase.exception.EmptyAccountService;
import com.zhxh.modulebase.service.IAccountService;
import com.zhxh.modulebase.service.IShareService;

public class ServiceFactory {

    private IAccountService accountService;
    private IShareService shareService;

    /**
     * 禁止外部创建 ServiceFactory 对象
     */
    private ServiceFactory() {
    }

    /**
     * 通过静态内部类方式实现 ServiceFactory 的单例
     */
    public static ServiceFactory getInstance() {
        return Inner.serviceFactory;
    }

    private static class Inner {
        private static ServiceFactory serviceFactory = new ServiceFactory();
    }


    /* *******************登录模块**********************************/
    /**
     * 接收 Login 组件实现的 Service 实例
     */
    public void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 返回 Login 组件的 Service 实例
     */
    public IAccountService getAccountService() {
        if (accountService == null) {
            accountService = new EmptyAccountService();
        }
        return accountService;
    }


    /* *******************分享模块**********************************/

    public IShareService getShareService() {
        return shareService;
    }

    public void setShareService(IShareService shareService) {
        this.shareService = shareService;
    }
}
