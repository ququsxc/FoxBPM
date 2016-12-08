package org.foxbpm.disclosure.model;

import org.apache.commons.lang3.StringUtils;
import org.foxbpm.engine.impl.entity.UserEntity;
import org.foxbpm.engine.impl.identity.Authentication;

public class Disclosure {
	private String id;
	private String ownerId;
	private String ownerContact;
	private String workerId;
	private String workerContact;
	private String designerId;
	private String designerContact;
	private String supervisorId;
	private String supervisorContact;
	private String houseAddress;
	private String houseType;
	private String houseArea;
	private String housePicture;
	private String decorationType;
	private String decorationStyle;
	private String decorationCompany;
	private Integer waterElectricityConfirmed;
	private Integer materialPlacingConfirmed;
	private Integer drainageConfirmed;
	private Integer deviceKeepConfirmed;
	private String constructionPermit;
	private String constructionPass;
	private String depositBill;
	private String houseStateTable;
	private String constructionPlanTable;
	private String supervisionPlanTable;
	private String designDrawing;

	public String getHousePicture() {
		return housePicture;
	}

	public void setHousePicture(String housePicture) {
		this.housePicture = housePicture;
	}

	public String getHouseAddress() {
		return houseAddress;
	}

	public void setHouseAddress(String houseAddress) {
		this.houseAddress = houseAddress;
	}

	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public String getHouseArea() {
		return houseArea;
	}

	public void setHouseArea(String houseArea) {
		this.houseArea = houseArea;
	}

	public String getDecorationType() {
		return decorationType;
	}

	public void setDecorationType(String decorationType) {
		this.decorationType = decorationType;
	}

	public String getDecorationStyle() {
		return decorationStyle;
	}

	public void setDecorationStyle(String decorationStyle) {
		this.decorationStyle = decorationStyle;
	}

	public String getDecorationCompany() {
		return decorationCompany;
	}

	public void setDecorationCompany(String decorationCompany) {
		this.decorationCompany = decorationCompany;
	}

	public Integer getWaterElectricityConfirmed() {
		return waterElectricityConfirmed;
	}

	public void setWaterElectricityConfirmed(Integer waterElectricityConfirmed) {
		this.waterElectricityConfirmed = waterElectricityConfirmed;
	}

	public Integer getMaterialPlacingConfirmed() {
		return materialPlacingConfirmed;
	}

	public void setMaterialPlacingConfirmed(Integer materialPlacingConfirmed) {
		this.materialPlacingConfirmed = materialPlacingConfirmed;
	}

	public Integer getDrainageConfirmed() {
		return drainageConfirmed;
	}

	public void setDrainageConfirmed(Integer drainageConfirmed) {
		this.drainageConfirmed = drainageConfirmed;
	}

	public Integer getDeviceKeepConfirmed() {
		return deviceKeepConfirmed;
	}

	public void setDeviceKeepConfirmed(Integer deviceKeepConfirmed) {
		this.deviceKeepConfirmed = deviceKeepConfirmed;
	}

	public String getConstructionPermit() {
		return constructionPermit;
	}

	public void setConstructionPermit(String constructionPermit) {
		this.constructionPermit = constructionPermit;
	}

	public String getConstructionPass() {
		return constructionPass;
	}

	public void setConstructionPass(String constructionPass) {
		this.constructionPass = constructionPass;
	}

	public String getDepositBill() {
		return depositBill;
	}

	public void setDepositBill(String depositBill) {
		this.depositBill = depositBill;
	}

	public String getHouseStateTable() {
		return houseStateTable;
	}

	public void setHouseStateTable(String houseStateTable) {
		this.houseStateTable = houseStateTable;
	}

	public String getConstructionPlanTable() {
		return constructionPlanTable;
	}

	public void setConstructionPlanTable(String constructionPlanTable) {
		this.constructionPlanTable = constructionPlanTable;
	}

	public String getSupervisionPlanTable() {
		return supervisionPlanTable;
	}

	public void setSupervisionPlanTable(String supervisionPlanTable) {
		this.supervisionPlanTable = supervisionPlanTable;
	}

	public String getDesignDrawing() {
		return designDrawing;
	}

	public void setDesignDrawing(String designDrawing) {
		this.designDrawing = designDrawing;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getOwnerContact() {
		return ownerContact;
	}

	public void setOwnerContact(String ownerContact) {
		this.ownerContact = ownerContact;
	}

	public String getWorkerId() {
		return workerId;
	}

	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}

	public String getWorkerContact() {
		return workerContact;
	}

	public void setWorkerContact(String workerContact) {
		this.workerContact = workerContact;
	}

	public String getDesignerId() {
		return designerId;
	}

	public void setDesignerId(String designerId) {
		this.designerId = designerId;
	}

	public String getDesignerContact() {
		return designerContact;
	}

	public void setDesignerContact(String designerContact) {
		this.designerContact = designerContact;
	}

	public String getSupervisorId() {
		return supervisorId;
	}

	public void setSupervisorId(String supervisorId) {
		this.supervisorId = supervisorId;
	}

	public String getSupervisorContact() {
		return supervisorContact;
	}

	public void setSupervisorContact(String supervisorContact) {
		this.supervisorContact = supervisorContact;
	}

	private String getUserName(String userId) {
		if (StringUtils.isNotEmpty(userId)) {
			UserEntity user = Authentication.selectUserByUserId(userId);
			if (user != null) {
				return user.getUserName();
			}
		}
		return null;
	}

	public String getOwnerName() {
		return getUserName(ownerId);
	}

	public String getWorkerName() {
		return getUserName(workerId);
	}

	public String getDesignerName() {
		return getUserName(designerId);
	}

	public String getSupervisorName() {
		return getUserName(supervisorId);
	}
}
