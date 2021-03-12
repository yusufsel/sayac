package ysf.sayac;

import java.util.Date;

public class GunlukTuketim {
	private Date tarih;
	private double ilkEndex;
	private double sonEndex;
	private double tuketim;
	
	
	@Override
	public String toString() {
		return "GunlukTuketim [tarih=" + tarih + ", ilkEndex=" + ilkEndex + ", sonEndex=" + sonEndex + ", tuketim="
				+ tuketim + "]";
	}
	public GunlukTuketim(Date tarih, double ilkEndex, double sonEndex, double tuketim) {
		super();
		this.tarih = tarih;
		this.ilkEndex = ilkEndex;
		this.sonEndex = sonEndex;
		this.tuketim = tuketim;
	}
	public Date getTarih() {
		return tarih;
	}
	public void setTarih(Date tarih) {
		this.tarih = tarih;
	}
	public double getIlkEndex() {
		return ilkEndex;
	}
	public void setIlkEndex(double ilkEndex) {
		this.ilkEndex = ilkEndex;
	}
	public double getSonEndex() {
		return sonEndex;
	}
	public void setSonEndex(double sonEndex) {
		this.sonEndex = sonEndex;
	}
	public double getTuketim() {
		return tuketim;
	}
	public void setTuketim(double tuketim) {
		this.tuketim = tuketim;
	}
	
}
