package com.bonree.ants.manager.proto;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年12月3日 下午3:39:18
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: a
 ******************************************************************************/
public class RequestResponseProto {

    private String actionType;

    private BeatsRequestProto beatsRequest;
    private BeatsResponseProto beatsResponse;

    private DatasRequestProto datasRequest;
    private DatasResponseProto datasResponse;

    private CmdsRequestProto cmdsRequest;
    private CmdsResponseProto cmdsResponse;

    private FilesRequestProto filesRequest;
    private FilesResponseProto filesResponse;

    public RequestResponseProto(String actionType) {
        this.actionType = actionType;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public BeatsRequestProto getBeatsRequest() {
        return beatsRequest;
    }

    public void setBeatsRequest(BeatsRequestProto beatsRequest) {
        this.beatsRequest = beatsRequest;
    }

    public BeatsResponseProto getBeatsResponse() {
        return beatsResponse;
    }

    public void setBeatsResponse(BeatsResponseProto beatsResponse) {
        this.beatsResponse = beatsResponse;
    }

    public DatasRequestProto getDatasRequest() {
        return datasRequest;
    }

    public void setDatasRequest(DatasRequestProto datasRequest) {
        this.datasRequest = datasRequest;
    }

    public DatasResponseProto getDatasResponse() {
        return datasResponse;
    }

    public void setDatasResponse(DatasResponseProto datasResponse) {
        this.datasResponse = datasResponse;
    }

    public CmdsRequestProto getCmdsRequest() {
        return cmdsRequest;
    }

    public void setCmdsRequest(CmdsRequestProto cmdsRequest) {
        this.cmdsRequest = cmdsRequest;
    }

    public CmdsResponseProto getCmdsResponse() {
        return cmdsResponse;
    }

    public void setCmdsResponse(CmdsResponseProto cmdsResponse) {
        this.cmdsResponse = cmdsResponse;
    }

    public FilesRequestProto getFilesRequest() {
        return filesRequest;
    }

    public void setFilesRequest(FilesRequestProto filesRequest) {
        this.filesRequest = filesRequest;
    }

    public FilesResponseProto getFilesResponse() {
        return filesResponse;
    }

    public void setFilesResponse(FilesResponseProto filesResponse) {
        this.filesResponse = filesResponse;
    }

}
