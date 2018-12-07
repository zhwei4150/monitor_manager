package com.bonree.ants.manager.proto;

import com.bonree.ants.manager.command.CmdProperty;

public class CmdsResponseProto {

    public CmdsResponseProto(ReturnCode code) {
        this.code = code;
    }

    private ReturnCode code;

    private CmdProperty cmdProperty;

    private String jsonData;

    public CmdProperty getCmdProperty() {
        return cmdProperty;
    }

    public void setCmdProperty(CmdProperty cmdProperty) {
        this.cmdProperty = cmdProperty;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public ReturnCode getCode() {
        return code;
    }

    public void setCode(ReturnCode code) {
        this.code = code;
    }

}
