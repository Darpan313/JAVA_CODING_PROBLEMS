package OOP.CarRentalSystem.models;

import OOP.CarRentalSystem.enums.EquipmentType;

public class Equipment {
    private final EquipmentType equipmentType;
    private final double dailyRate;

    public Equipment(EquipmentType equipmentType, double dailyRate) {
        this.equipmentType = equipmentType;
        this.dailyRate = dailyRate;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "equipmentType=" + equipmentType +
                ", dailyRate=" + dailyRate + "/day" +
                '}';
    }
}
