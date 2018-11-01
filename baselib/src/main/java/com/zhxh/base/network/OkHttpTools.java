package com.zhxh.base.network;

import com.zhxh.base.exception.ApplicationException;
import com.zhxh.base.exception.ApplicationTimeOutException;
import com.zhxh.base.utils.CommonUtils;
import com.zhxh.base.utils.LogUtils;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by zhxh on 2018/11/1
 */
public class OkHttpTools {

    public static final int timeOutExcepState = 1;

    public static final int appExcepState = 2;

    public OkHttpTools() {
    }

    public void httpGet(String url, final DataPackage dataPackage)
            throws ApplicationException, ApplicationTimeOutException {

        if (CommonUtils.isNull(url))
            return;

        String queryString = dataPackage.getRequestData();

        if (dataPackage.getRequestID() == RequestCommand.COMMAND_STOCK_TOPIC_COUNT) {

            if (!CommonUtils.isNull(queryString)) {

                url += ("?" + queryString);
            }

        } else {

            if (!CommonUtils.isNull(queryString)) {

                url += ("?" + queryString + "&" + getUrlParams(dataPackage.isSkinMode()));
            } else {

                url += ("?" + getUrlParams(dataPackage.isSkinMode()));
            }
        }

        LogUtils.d("geturl", url);

        try {

            tempDataCache = new TempDataCache();

            tempDataCache.state = 0;
            tempDataCache.responseData = OkHttpUtil.getStringFromServer(url, dataPackage.getRequestID());

            tempDataCache.setDataToPackage(dataPackage);

        } catch (SocketTimeoutException sex) {

//			sex.printStackTrace();
            throw new ApplicationTimeOutException(sex.getMessage());

        } catch (UnknownHostException uex) {

//			uex.printStackTrace();
            throw new ApplicationException(uex.getMessage());

        } catch (Exception e) {

//			e.printStackTrace();
            throw new ApplicationException(e.getMessage());

        } finally {

            tempDataCache = null;
        }
    }

    public void httpPost(String url, final DataPackage dataPackage)
            throws ApplicationException, ApplicationTimeOutException {

        if (CommonUtils.isNull(url))
            return;

        try {

            url = url + "?" + getUrlParams(dataPackage.isSkinMode());

            MediaType contentType = MediaType.parse("application/x-www-form-urlencoded");

            String content = "";

            String queryString = dataPackage.getRequestData();
            if (!CommonUtils.isNull(queryString)) {

                int requestID = dataPackage.getRequestID();

                if (isPostRequest(requestID)) {

                    if (requestID == RequestCommand.COMMAND_FOREIGN_POSITION_WIHT_VESION_ID) {
                        //3:代表增加 日内融 4 代表增加 港股 IPO 入口
                        url = url + "&versionid=" + "4" + "&deviceid=" + CommonDataManager.DEVICEID;
                    }

                    content = "param=" + TripleDesUtil.encryptMode(queryString)
                            + "&check=" + StringUtils.toMD5(queryString);

                } else if (requestID == RequestCommand.COMMAND_COLLECT_USERINFO
                        || requestID == RequestCommand.COMMAND_DEVICE_REG
                        || requestID == RequestCommand.COMMAND_USER_QUIT) {

                    content = "deviceparams=" + TripleDesUtil.encryptMode(queryString)
                            + "&sign=" + StringUtils.toMD5(queryString);

                } else if (requestID == RequestCommand.COMMAND_USER_GET_VERIFY_CODE) {

                    content = "sendParams=" + TripleDesUtil.encryptMode(queryString)
                            + "&sign=" + StringUtils.toMD5(queryString);

                } else {

                    content = queryString;
                }

                LogUtils.d("posturl", url + "?" + queryString);
            }

            RequestBody requestBody = RequestBody.create(contentType, content);

            tempDataCache = new TempDataCache();

            tempDataCache.state = 0;
            tempDataCache.responseData = OkHttpUtil.post(url, requestBody, dataPackage.getRequestID());

            tempDataCache.setDataToPackage(dataPackage);

        } catch (SocketTimeoutException sex) {
//			sex.printStackTrace();
            throw new ApplicationTimeOutException(sex.getMessage());

        } catch (UnknownHostException uex) {
//			uex.printStackTrace();
            throw new ApplicationException(uex.getMessage());

        } catch (Exception e) {
//			e.printStackTrace();
            throw new ApplicationException(e.getMessage());

        } finally {

            tempDataCache = null;
        }
    }

    private TempDataCache tempDataCache = null;

    private class TempDataCache {

        int state = -1;

        byte[] responseData = null;

        void setDataToPackage(final DataPackage dataPackage) {

            dataPackage.setState(state);
            dataPackage.setTempData(responseData);
        }
    }

    /***
     * post加密请求
     * @param requestID
     * @return
     */
    public static boolean isPostRequest(int requestID) {

        return false;
    }


    private static String getUrlParams(boolean isSkinMode) {
        if (isSkinMode) {
            return "s=" + CommonDataManager.CHANNEL + "&version=" + CommonDataManager.VERSIONNAME + "&packtype=" + CommonDataManager.PACKTYPE + "&night=" + MyApplication.SKIN_MODE;
        } else {
            return "s=" + CommonDataManager.CHANNEL + "&version=" + CommonDataManager.VERSIONNAME + "&packtype=" + CommonDataManager.PACKTYPE;

        }
    }

}
