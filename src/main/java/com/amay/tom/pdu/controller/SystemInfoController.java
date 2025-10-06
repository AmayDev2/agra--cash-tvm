package com.amay.tom.pdu.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.dto.MasterConfigInfoDTO;
import com.amay.tom.model.version.MasterConfigInfoEntity;
import com.amay.tom.model.version.MasterConfigInfoMapper;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tom.repository.version.VersionRepository;
import com.amay.tom.utils.NetworkUtils;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.bnr.BNRIntegration;
import com.jxfs.events.JxfsException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import org.tinylog.Logger;

import java.util.List;

public class SystemInfoController {
    private final Agent agent;
    private final SceneManager sceneManager;
    public SystemInfoController(Agent agent, SceneManager sceneManager) {
        this.agent = agent;
        this.sceneManager = sceneManager;
    }

    @FXML
    private Label equipLabel, peakTimeLabel, softwareLabel, fareLabel, userLabel, productLabel,
            profileLabel, calenderLabel, topoLabel, readerLabel, buisinessLabel,
            equipIdLabel, equipIpLabel, BnrLabel, coinLabel;


    @FXML
    public void initialize() {
        try {
            VersionRepository repo = agent.getVersionRepository();
            List<MasterConfigInfoEntity> allVersions = repo.findAll(); // fetch all
            if (allVersions != null && !allVersions.isEmpty()) {
                MasterConfigInfoEntity entity = allVersions.get(0);
                MasterConfigInfoDTO dto = MasterConfigInfoMapper.entityToDto(entity);

                equipLabel.setText(dto.getConfigVer());
                peakTimeLabel.setText(dto.getPeakTimeVer());
                softwareLabel.setText(dto.getTvmSwVer());
                fareLabel.setText(dto.getFareConfig());
                userLabel.setText(dto.getUserVer());
                productLabel.setText(dto.getProductConfig());
                profileLabel.setText(dto.getProfileVer());
                calenderLabel.setText(dto.getCalenderConfig());
                topoLabel.setText(dto.getTopologyConfig());
                readerLabel.setText(dto.getTrSwVer() != null ? dto.getTrSwVer() : "N/A");
                buisinessLabel.setText(dto.getBusinessDayVer());

                equipIpLabel.setText(NetworkUtils.getLocalIpAddress());
//                equipIdLabel.setText(agent.getSystemConfig().getEquipmentId());
                equipIdLabel.setText(agent.getSystemConfig().getCurrentEquipment().getEquipmentId());

//                BnrLabel.setText(agent.getSystemConfig().getBnrVersion());
//                coinLabel.setText(agent.getSystemConfig().getCoinModuleVersion());
            }

        } catch (Exception e) {
            Logger.error("Error fetching version info: " + e.getMessage());
        }
    }


    public void onBack(ActionEvent actionEvent) {
        sceneManager.back();
        actionEvent.consume();
    }
}
