package com.bonree.ants.manager.agent.command.imp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bonree.ants.manager.agent.command.AbstractCmdExecutor;
import com.bonree.ants.manager.command.CmdCell;
import com.bonree.ants.manager.command.CmdProperty;
import com.bonree.ants.manager.command.CmdProperty.CmdCellDetail;
import com.bonree.ants.manager.util.ConfigUtil;

public class DefaultCmdExecutor extends AbstractCmdExecutor {

    private Object cmdConfig;
    private Map<String, CmdCell> cmdCells;

    private DefaultCmdExecutor(String name, Object cmdConfig) {
        super(name);
        this.cmdConfig = cmdConfig;
        cmdCells = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public String executeCmd(CmdCellDetail cellDetail, String cmdJson) throws Exception {
        CmdCell cell = cmdCells.get(cellDetail.getName());

        if (cell != null) {
            return cell.executeCmd(cmdJson, cmdConfig);
        }

        Class<?> cls = Class.forName(cellDetail.getExec());
        cell = (CmdCell) cls.newInstance();
        cmdCells.put(cellDetail.getName(), cell);
        return cell.executeCmd(cmdJson, cmdConfig);
    }

    public static AbstractCmdExecutor getInstance(CmdProperty cmdProperty) throws Exception {
        String name = cmdProperty.getName();
        Class<?> cls = Class.forName(cmdProperty.getCmdConfigClass());
        Object obj = ConfigUtil.constructObjectFromYaml(cmdProperty.getCmdConfig(), cls);
        return new DefaultCmdExecutor(name, obj);
    }

}
