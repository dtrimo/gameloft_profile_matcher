package com.gameloft.profilematcher.view.mapper;

import com.gameloft.profilematcher.model.Device;
import com.gameloft.profilematcher.view.model.DeviceView;
import org.springframework.stereotype.Component;

@Component
public class DeviceMapper {

    public DeviceView map(Device device) {
        if (device == null) {
            return null;
        }
        DeviceView view = new DeviceView();
        view.setId(device.getId());
        view.setFirmware(device.getFirmware());
        view.setCarrier(device.getCarrier());
        view.setModel(device.getModel());
        return view;
    }

}
