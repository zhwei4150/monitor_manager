package com.bonree.ants.manager.server.command;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.bonree.ants.manager.command.CmdProperty;
import com.bonree.ants.manager.command.CmdProperty.CmdCellDetail;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CmdManager {
    private final Map<String, CmdProperty> cmds = Maps.newHashMap();

    public CmdManager(List<CmdProperty> cmdProperties) {
        Preconditions.checkNotNull(cmdProperties);
        cmdProperties.stream().forEach(cmd -> cmds.put(cmd.getName(), cmd));
    }

    public boolean checkCmdExist(String moduleName, String cmdName) {
        if (cmds.containsKey(moduleName)) {
            List<CmdCellDetail> cells = cmds.get(moduleName).getCommands();
            if (cells != null) {
                return cells.stream().filter(cell -> StringUtils.equalsIgnoreCase(cell.getName(), cmdName)).findFirst()
                        .isPresent();
            }
        }
        return false;
    }

    public CmdProperty getCmdModule(String name) {
        return cmds.get(name);
    }

    public CmdProperty getSpecifyCmd(String module, String cmdName) {
        CmdProperty source = cmds.get(module);
        CmdProperty cmdProperty = null;
        if (source != null) {
            cmdProperty = new CmdProperty();
            cmdProperty.setName(source.getName());
            cmdProperty.setCmdConfig(source.getCmdConfig());
            cmdProperty.setCmdConfigClass(source.getCmdConfigClass());
            cmdProperty.setAgentNodes(source.getAgentNodes());

            List<CmdCellDetail> cmdDetails = source.getCommands();
            if (cmdDetails != null && !cmdDetails.isEmpty()) {
                Optional<CmdCellDetail> detailOpt = cmdDetails.stream().filter(detail -> StringUtils
                        .equalsIgnoreCase(detail.getName(), cmdName)).findFirst();
                if (detailOpt.isPresent()) {
                    cmdProperty.setCommands(Lists.newArrayList(detailOpt.get()));
                }
            }
        }
        return cmdProperty;
    }
}
