//package com.noetic.zong_billing.entities;
//
//import javax.persistence.*;
//import java.sql.Timestamp;
//import java.util.Objects;
//
//@Entity
//@Table(name = "ucip_charging", schema = "public", catalog = "ucip_db")
//public class UcipChargingEntity {
//    private long id;
//    private String msisdn;
//    private String amount;
//    private Integer isCharged;
//    private Integer isPostpaid;
//    private Timestamp cdate;
//    private Integer ucipResponse;
//
//    @Id
//    @Column(name = "id")
//    @SequenceGenerator(name = "ucip_charging_id_seq",sequenceName = "ucip_charging_id_seq",allocationSize=1, initialValue=1)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "ucip_charging_id_seq")
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//    @Basic
//    @Column(name = "msisdn")
//    public String getMsisdn() {
//        return msisdn;
//    }
//
//    public void setMsisdn(String msisdn) {
//        this.msisdn = msisdn;
//    }
//
//    @Basic
//    @Column(name = "amount")
//    public String getAmount() {
//        return amount;
//    }
//
//    public void setAmount(String amount) {
//        this.amount = amount;
//    }
//
//    @Basic
//    @Column(name = "is_charged")
//    public Integer getIsCharged() {
//        return isCharged;
//    }
//
//    public void setIsCharged(Integer isCharged) {
//        this.isCharged = isCharged;
//    }
//
//    @Basic
//    @Column(name = "is_postpaid")
//    public Integer getIsPostpaid() {
//        return isPostpaid;
//    }
//
//    public void setIsPostpaid(Integer isPostpaid) {
//        this.isPostpaid = isPostpaid;
//    }
//
//    @Basic
//    @Column(name = "cdate")
//    public Timestamp getCdate() {
//        return cdate;
//    }
//
//    public void setCdate(Timestamp cdate) {
//        this.cdate = cdate;
//    }
//
//    @Basic
//    @Column(name = "ucip_response")
//    public Integer getUcipResponse() {
//        return ucipResponse;
//    }
//
//    public void setUcipResponse(Integer ucipResponse) {
//        this.ucipResponse = ucipResponse;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        UcipChargingEntity that = (UcipChargingEntity) o;
//        return id == that.id &&
//                Objects.equals(msisdn, that.msisdn) &&
//                Objects.equals(amount, that.amount) &&
//                Objects.equals(isCharged, that.isCharged) &&
//                Objects.equals(isPostpaid, that.isPostpaid) &&
//                Objects.equals(cdate, that.cdate) &&
//                Objects.equals(ucipResponse, that.ucipResponse);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, msisdn, amount, isCharged, isPostpaid, cdate, ucipResponse);
//    }
//}
