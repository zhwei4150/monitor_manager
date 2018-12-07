package com.bonree.ants.manager.command;

import java.util.List;

public class CmdProperty {

    private String name;
    private String cmdConfigClass;
    private String cmdConfig;

    private String agentNodes;
    private List<CmdCellDetail> commands;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCmdConfigClass() {
        return cmdConfigClass;
    }

    public void setCmdConfigClass(String cmdConfigClass) {
        this.cmdConfigClass = cmdConfigClass;
    }

    public String getCmdConfig() {
        return cmdConfig;
    }

    public void setCmdConfig(String cmdConfig) {
        this.cmdConfig = cmdConfig;
    }

    public String getAgentNodes() {
        return agentNodes;
    }

    public void setAgentNodes(String agentNodes) {
        this.agentNodes = agentNodes;
    }

    public List<CmdCellDetail> getCommands() {
        return commands;
    }

    public void setCommands(List<CmdCellDetail> commands) {
        this.commands = commands;
    }

    @Override
    public String toString() {
        return "CmdProperty [name=" + name + ", cmdConfigClass=" + cmdConfigClass + ", cmdConfig=" + cmdConfig + ", agentNodes=" + agentNodes + ", commands=" + commands + "]";
    }

    public static class CmdCellDetail {
        public final static String TYPE_LOCAL = "local";
        public final static String TYPE_REMOTE = "remote";

        private String name;
        private String exec;
        private String type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getExec() {
            return exec;
        }

        public void setExec(String exec) {
            this.exec = exec;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Command [name=" + name + ", exec=" + exec + ", type=" + type + "]";
        }

    }

}
