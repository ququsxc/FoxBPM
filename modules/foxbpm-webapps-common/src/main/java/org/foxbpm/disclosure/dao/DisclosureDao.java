package org.foxbpm.disclosure.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.foxbpm.disclosure.model.Disclosure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DisclosureDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final RowMapper<Disclosure> ROW_MAPPER = new BeanPropertyRowMapper<>(Disclosure.class);

	public int save(Disclosure disclosure) {
		String sql = "insert into tb_disclosure(id,owner_id,owner_contact,worker_id,worker_contact,designer_id,designer_contact,supervisor_id,supervisor_contact,house_address,house_type,house_area,house_picture,decoration_type,decoration_style,decoration_company) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		return jdbcTemplate.update(sql, disclosure.getId(), disclosure.getOwnerId(), disclosure.getOwnerContact(), disclosure.getWorkerId(), disclosure.getWorkerContact(), disclosure.getDesignerId(), disclosure.getDesignerContact(), disclosure.getSupervisorId(), disclosure.getSupervisorContact(), disclosure.getHouseAddress(), disclosure.getHouseType(), disclosure.getHouseArea(), disclosure.getHousePicture(), disclosure.getDecorationType(), disclosure.getDecorationStyle(), disclosure.getDecorationCompany());
	}

	public Disclosure getCommonInfo(String id) {
		return jdbcTemplate.queryForObject("select id,owner_id,owner_contact,worker_id,worker_contact,designer_id,designer_contact,supervisor_id,supervisor_contact,house_address,house_type,house_area,house_picture,decoration_type,decoration_style,decoration_company from tb_disclosure where id=?", ROW_MAPPER, id);
	}

	public Disclosure get(String id) {
		return jdbcTemplate.queryForObject("select * from tb_disclosure where id=?", ROW_MAPPER, id);
	}

	public int update(Disclosure disclosure) {
		StringBuffer sql = new StringBuffer();
		List<Object> param = new ArrayList<>();
		sql.append("update tb_disclosure set ");
		if (disclosure.getWaterElectricityConfirmed() != null) {
			sql.append("water_electricity_confirmed=?,");
			param.add(disclosure.getWaterElectricityConfirmed());
		}
		if (disclosure.getMaterialPlacingConfirmed() != null) {
			sql.append("material_placing_confirmed=?,");
			param.add(disclosure.getMaterialPlacingConfirmed());
		}
		if (disclosure.getDrainageConfirmed() != null) {
			sql.append("drainage_confirmed=?,");
			param.add(disclosure.getDrainageConfirmed());
		}
		if (disclosure.getDeviceKeepConfirmed() != null) {
			sql.append("device_keep_confirmed=?,");
			param.add(disclosure.getDeviceKeepConfirmed());
		}
		if (StringUtils.isNotEmpty(disclosure.getConstructionPermit())) {
			sql.append("construction_permit=?,");
			param.add(disclosure.getConstructionPermit());
		}
		if (StringUtils.isNotEmpty(disclosure.getConstructionPass())) {
			sql.append("construction_pass=?,");
			param.add(disclosure.getConstructionPass());
		}
		if (StringUtils.isNotEmpty(disclosure.getDepositBill())) {
			sql.append("deposit_bill=?,");
			param.add(disclosure.getDepositBill());
		}
		if (StringUtils.isNotEmpty(disclosure.getHouseStateTable())) {
			sql.append("house_state_table=?,");
			param.add(disclosure.getHouseStateTable());
		}
		if (StringUtils.isNotEmpty(disclosure.getConstructionPlanTable())) {
			sql.append("construction_plan_table=?,");
			param.add(disclosure.getConstructionPlanTable());
		}
		if (StringUtils.isNotEmpty(disclosure.getSupervisionPlanTable())) {
			sql.append("supervision_plan_table=?,");
			param.add(disclosure.getSupervisionPlanTable());
		}
		if (StringUtils.isNotEmpty(disclosure.getDesignDrawing())) {
			sql.append("design_drawing=?,");
			param.add(disclosure.getDesignDrawing());
		}
		if (param.isEmpty()) {
			return 0;
		} else {
			sql.deleteCharAt(sql.length() - 1).append(" where id=?");
			param.add(disclosure.getId());
			return jdbcTemplate.update(sql.toString(), param.toArray());
		}
	}
}
