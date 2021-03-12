package ysf.sayac;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

@SpringBootApplication
@RestController
public class SayacApplication {

	public static void main(String[] args) {
		SpringApplication.run(SayacApplication.class, args);		
	}
	
	@GetMapping("/{yil}/{ay}/{ilkEndex}/{sonEndex}/{haftaIciMin}/{haftaIciMax}/{haftaSonuMin}/{haftaSonuMax}")
	  void all(HttpServletResponse response,
              HttpServletRequest request,
              @PathVariable int yil,
              @PathVariable int ay,
              @PathVariable double ilkEndex,
              @PathVariable double sonEndex,
              @PathVariable double haftaIciMin,
              @PathVariable double haftaIciMax,
              @PathVariable double haftaSonuMin,
              @PathVariable double haftaSonuMax) {
		
		List<GunlukTuketim> tuketimler = new SayacApplication().doIt(yil, ay, ilkEndex, sonEndex, haftaIciMin, haftaIciMax, haftaSonuMin, haftaSonuMax);
		try {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			exportIt(tuketimler,response.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	
	public void exportIt(List<GunlukTuketim> tuketimler,OutputStream os) throws IOException{
		String excelFilePath = "Reviews-export.xlsx";
		
		XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Reviews");
        
        writeHeaderLine(sheet);
        
        writeDataLines(tuketimler, workbook, sheet);

//        FileOutputStream outputStream = new FileOutputStream(excelFilePath);
//        workbook.write(outputStream);
        workbook.write(os);
        
        workbook.close();
	}
	
	 private void writeHeaderLine(XSSFSheet sheet) {
		 
	        Row headerRow = sheet.createRow(0);
	 
	        Cell headerCell = headerRow.createCell(0);
	        headerCell.setCellValue("Tarih");
	 
	        headerCell = headerRow.createCell(1);
	        headerCell.setCellValue("İlk Endex");
	 
	        headerCell = headerRow.createCell(2);
	        headerCell.setCellValue("Son Endex");
	 
	        headerCell = headerRow.createCell(3);
	        headerCell.setCellValue("Tüketim");
	 
	    }
	 
	    private void writeDataLines(List<GunlukTuketim> tuketimler, XSSFWorkbook workbook,
	            XSSFSheet sheet){
	        int rowCount = 1;
	 
	        for(GunlukTuketim t:tuketimler) {
	            
	        	Row row = sheet.createRow(rowCount++);
	 
	            int columnCount = 0;
	            Cell cell = row.createCell(columnCount++);
	            cell.setCellValue(t.getTarih().toString());
	 
	            cell = row.createCell(columnCount++);
	            cell.setCellValue(t.getIlkEndex());
	            
	            cell = row.createCell(columnCount++);
	            cell.setCellValue(t.getSonEndex());
	 
	            cell = row.createCell(columnCount);
	            cell.setCellValue(t.getTuketim());
	 
	        }
	    }
	
	public List<GunlukTuketim> doIt(int yil,int ay,
			double ilkEndex,double sonEndex,
			double haftaIciMin,double haftaIciMax,
			double haftaSonuMin,double haftaSonuMax){
		
		List<GunlukTuketim> tuketimler = new ArrayList<GunlukTuketim>();
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE,1);
		cal.set(Calendar.YEAR,yil);
		cal.set(Calendar.MONTH,ay-1);
		
		int sonGun = cal.getActualMaximum(Calendar.DATE);
		
		
		double toplamConrolTuketimi = 0;
		GunlukTuketim gt;
		
		int haftaIciGunSayisi = 0;
		for(int i=1;i<=sonGun;i++){
			double tuketim;
			if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				tuketim = rastgeleKullanimVer(haftaSonuMin,haftaSonuMax);
			}else{
				tuketim = rastgeleKullanimVer(haftaIciMin,haftaIciMax);
				haftaIciGunSayisi += 1;
			}
			toplamConrolTuketimi += tuketim;
			gt = new GunlukTuketim(cal.getTime(), 0, 0, tuketim);
			tuketimler.add(gt);
			cal.add(Calendar.DATE, 1);			
		}
		
		double haftaIciDuzeltme = ((sonEndex-ilkEndex)-toplamConrolTuketimi)/haftaIciGunSayisi;
				
		double sonControlEndex = ilkEndex;
		for(GunlukTuketim t:tuketimler){
			Calendar c = Calendar.getInstance();
			c.setTime(t.getTarih());
			if(!(c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY 
					|| c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)){
				t.setTuketim(t.getTuketim() + haftaIciDuzeltme); ;				
			}
			t.setIlkEndex(sonControlEndex);
			t.setSonEndex(sonControlEndex+=t.getTuketim()); 
			
			t.setIlkEndex(Math.round(t.getIlkEndex()*10000)/10000d);
			t.setSonEndex(Math.round(t.getSonEndex()*10000)/10000d);
			t.setTuketim(Math.round(t.getTuketim()*10000)/10000d);
			
			
			
//			System.out.println(t.toString());
		}
		
//		try {
//			exportIt(tuketimler);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return tuketimler;
		
	}
	
	Random rnd = new Random();
	public double rastgeleKullanimVer(double min,double max){
		return (rnd.nextFloat() * (max - min)) + min;
	}	
	
	
	
	

}
