import java.io.Writer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ubot.kyc.comm.MailServiceThread;
import com.ubot.kyc.common.CurrentDate;
import com.ubot.kyc.db.Admonlist_Diff_DailyDao;
import com.ubot.kyc.db.Admonlist_Diff_DailyVo;
import com.ubot.kyc.db.CustDao;
import com.ubot.kyc.db.CustVo;
import com.ubot.kyc.db.DbUtil;
import com.ubot.kyc.db.ThretRISDao;
import com.ubot.kyc.db.ThretRISVo;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class DayReportAML152 {
	static final char SBC_SPACE = 12288;
	static final String UTF8_BOM = "\uFEFF";
	static Connection con = null;
	static CurrentDate cd = new CurrentDate();
	static Admonlist_Diff_DailyDao dao = new Admonlist_Diff_DailyDao();
	static Admonlist_Diff_DailyVo vo = null;
	static ThretRISDao thretRISDao = new ThretRISDao();
	static ThretRISVo thretRISVo = null;
	static CustDao custDao = new CustDao();
	static CustVo custVo = null;
	static int pageB = 1;
	static int pageC = 1;
	static FileWriter fw0 = null;
	static FileWriter fw00 = null;
	static FileWriter fw01 = null;
	static String mailto = "";
	static Map<Integer, String> map = new HashMap<Integer, String>();
	static Map<Integer, String> mapGray = new HashMap<Integer, String>();	
	static Map<Integer, String> mapPIdY = new HashMap<Integer, String>();
	static Map<Integer, String> mapJointY = new HashMap<Integer, String>();
	static Map<Integer, String> mapBranchB = new HashMap<Integer, String>();
	static Map<Integer, String> mapBranchC = new HashMap<Integer, String>();
	static Map<String, String> mapToETL = new HashMap<String, String>();
	static Map<String, String> mapToETLThret = new HashMap<String, String>();
	static Map<String, String> mapToETLPID = new HashMap<String, String>();
	static String msg = "";
	public void DayRpt() {
		char changePage = (char)12;
		con = getConnection();		
		File file1 = new File("D:/KYC/FTP/LocalUser/fukycmf1/out/TAXLOST.txt");
		File file2 = new File("D:/RPT_152/TAXLOST"+(cd.getDate().replaceAll("-", "")) + ".txt");
		if(file1.exists()) {
			file1.delete();
		}
		if(file2.exists()) {
			file2.delete();
		}
		File fAllB = null;
		File fAllC = null;
		File fB = null;
		File fC = null;
		File path = new File("D:/dayRpt");
//		File gcisFile = new File("D:/GCIS152/data_1141121.txt");
		File gcisFile = new File("D:/GCIS152/data_" + cd.getMinguo()+cd.getMonth()+cd.getDay()+".txt");
		DataOutputStream outputB = null;
		DataOutputStream outputC = null;
		DataOutputStream outputAllB = null;
		DataOutputStream outputAllC = null;
		LineNumberReader lineNumberReaderB = null;
		LineNumberReader lineNumberReaderC = null;
		BufferedReader br = null;
		
		String year = cd.getMinguo();
		String month = cd.getMonth();
		String date = cd.getDay();
		String yesterDate = cd.getYesterDate();
		
		String block0 = "";
		String block1 = "";
		String block2 = "";
		String block3 = "";
		String block4 = "";
		String block5 = "";
		String block6 = "";
		String block7 = "";
		String block13 = "";
		
		String block8 = "";
		String block9 = "";
		String block10 = "";
		String block11 = "";
		String block12 = "";
		String block14 = "";
		String block15 = "";
		String block16 = "";
		String block17 = "";
		String block18 = "";
		
		File cFile = new File("D:/RPT_152/AML152B"+(cd.getDate().replaceAll("-", "")));
		if(!cFile.exists()) {cFile.mkdir();}
		
		map = dao.diffMap(con);
		mapGray = thretRISDao.getThret(con);
		mapPIdY = dao.pidNYMap(con);
		mapJointY = dao.jointNYMap(con);
		
		mapBranchB = dao.rptBranchB(con);
		mapBranchC = dao.rptBranchC(con);
		Properties properties = new Properties();
		String configFile = "File.properties";
		try {
			properties.load(new FileInputStream(configFile));
			mailto = properties.getProperty("MAILTO").trim();
			addLineMsg("開始時間：" + getDateTimeMicroSec());
			for(int j=0;j<mapBranchB.size();j++) {
				fAllB = new File("D:/dayRpt/" + mapBranchB.get(j) + "AML152-B.DAT");
				fAllB.createNewFile();
				outputAllB = new DataOutputStream(new FileOutputStream(fAllB, true));
				outputAllB.write(padLeft(changePage + "", 36, SBC_SPACE).getBytes("big5"));
				outputAllB.write(padLeft("關注客戶每日異動報表（歸屬單位）", 40, SBC_SPACE).getBytes("big5"));
				outputAllB.writeUTF("\r\n");
				outputAllB.write(padLeft("", 37, SBC_SPACE).getBytes("big5"));
				outputAllB.write(padLeft("（本報表保存年限為五年）", 40, SBC_SPACE).getBytes("big5"));
				outputAllB.writeUTF("\r\n");
				outputAllB.write(padLeft("報表代號：AML152-B", 50, SBC_SPACE).getBytes("big5"));
				outputAllB.writeUTF("\r\n");
				outputAllB.write(padLeft("歸屬單位：" + mapBranchB.get(j), 75, ' ').getBytes("big5"));
				outputAllB.write(padLeft(year+"年"+month+"月"+date+"日", 41, SBC_SPACE).getBytes("big5"));
				outputAllB.write(padLeft("頁次：1", 4, SBC_SPACE).getBytes("big5"));
				outputAllB.writeUTF("\r\n");
				outputAllB.writeUTF("\r\n");
				outputAllB.write(padLeft("統編", 25, SBC_SPACE).getBytes("big5"));
				outputAllB.write(padLeft("關注類型", 22, SBC_SPACE).getBytes("big5"));
				outputAllB.write(padLeft("名單資訊", 22, SBC_SPACE).getBytes("big5"));
				outputAllB.write(padLeft("備註", 8, SBC_SPACE).getBytes("big5"));
				outputAllB.writeUTF("\r\n");
				outputAllB.write(padLeft("戶名", 21, SBC_SPACE).getBytes("big5"));
				outputAllB.writeUTF("\r\n");
				outputAllB.writeUTF("\r\n");
				outputAllB.writeUTF("\r\n");
			}
			back:for(int i=0;i<mapGray.size();i++) {
				thretRISVo = thretRISDao.findById(mapGray.get(i), con);
				block8 = thretRISVo.getThret_Id_No();
				block9 = thretRISVo.getName();
				block10 = thretRISVo.getAccBranch();
				block11 = thretRISVo.getBrFlag();
				block12 = thretRISVo.getDailyDate();
				if(block10.equals("066")) {block10 = "013";}
				if(block10.equals("067")) {block10 = "023";}
				if(block10.equals("050")) {block10 = "026";}
				if(block10.equals("053")) {block10 = "007";}
				if(block10.equals("907")) {block10 = "900";}

				if(!dao.existBRA(block10, con)) {
					writeOut(fw0, "AML152B" , block8 + "|" + block9 + "|" + block10 + "|" + block11);
					continue back;}
				fB = new File("D:/dayRpt/" + block10 + "AML152-B.DAT");
				
				outputB = new DataOutputStream(new FileOutputStream(fB, true));		
				long fileBLength = fB.length();
				lineNumberReaderB = new LineNumberReader(new FileReader(fB));
				lineNumberReaderB.skip(fileBLength);
				
				int lineB = lineNumberReaderB.getLineNumber();
				if(lineB%54 == 0) {
					pageB = (lineB/54) + 1;
					outputB.write(padLeft(changePage + "", 35, SBC_SPACE).getBytes("big5"));
					outputB.write(padLeft("關注客戶每日異動報表（歸屬單位）", 40, SBC_SPACE).getBytes("big5"));
					outputB.writeUTF("\r\n");
					outputB.write(padLeft("", 37, SBC_SPACE).getBytes("big5"));
					outputB.write(padLeft("（本報表保存年限為五年）", 40, SBC_SPACE).getBytes("big5"));
					outputB.writeUTF("\r\n");
					outputB.write(padLeft("報表代號：AML152-B", 50, SBC_SPACE).getBytes("big5"));
					outputB.writeUTF("\r\n");
					outputB.write(padLeft("歸屬單位：" + block10, 75, ' ').getBytes("big5"));
					outputB.write(padLeft(year+"年"+month+"月"+date+"日", 41, SBC_SPACE).getBytes("big5"));
					outputB.write(padLeft("頁次：" + pageB, 4, SBC_SPACE).getBytes("big5"));
					outputB.writeUTF("\r\n");
					outputB.writeUTF("\r\n");
					outputB.write(padLeft("統編", 25, SBC_SPACE).getBytes("big5"));
					outputB.write(padLeft("關注類型", 22, SBC_SPACE).getBytes("big5"));
					outputB.write(padLeft("名單資訊", 22, SBC_SPACE).getBytes("big5"));
					outputB.write(padLeft("備註", 8, SBC_SPACE).getBytes("big5"));
					outputB.writeUTF("\r\n");
					outputB.write(padLeft("戶名", 21, SBC_SPACE).getBytes("big5"));
					outputB.writeUTF("\r\n");
					outputB.writeUTF("\r\n");
					outputB.writeUTF("\r\n");
				}
				outputB.write(padLeft(block8, 49, ' ').getBytes("big5"));
				outputB.write(padLeft("灰名單", 40, ' ').getBytes("big5"));
				outputB.write(padLeft("類型：列入灰名單", 18, SBC_SPACE).getBytes("big5"));
				outputB.writeUTF("\r\n");
				outputB.write(padLeft(block9, 46, SBC_SPACE).getBytes("big5"));
				outputB.write(padLeft("列入日期：" + thretRISDao.getDate(block8, con), 18, SBC_SPACE).getBytes("big5"));
				outputB.writeUTF("\r\n");
				outputB.write(padLeft("", 46, SBC_SPACE).getBytes("big5"));
				outputB.write(padLeft("", 18, SBC_SPACE).getBytes("big5"));
				outputB.writeUTF("\r\n");
				outputB.write(padLeft("", 46, SBC_SPACE).getBytes("big5"));
				outputB.write(padLeft("", 18, SBC_SPACE).getBytes("big5"));		
				outputB.writeUTF("\r\n");
				outputB.writeUTF("\r\n");
			}
				back:for(int i=0;i<map.size();i++) {
					vo = dao.findById(map.get(i), con);
					block0 = vo.getcId();
					block1 = vo.getName();
					block2 = vo.getWarningDate();
					block3 = vo.getExpireDate();
					block4 = dao.existGray(map.get(i).split("\\|")[0], con) ? thretRISDao.findById(map.get(i).split("\\|")[0], con).getAccBranch() : vo.getRmdBranch();
//					if(map.get(i).contains("R123534335A")){System.out.println(dao.existGray(map.get(i), con)); System.out.println("block4 : "+block4);}
					block5 = vo.getIssuer();
					block6 = vo.getDataType();
					if(block4.equals("066")) {block4 = "013";}
					if(block4.equals("067")) {block4 = "023";}
					if(block4.equals("050")) {block4 = "026";}
					if(block4.equals("053")) {block4 = "007";}
					if(block4.equals("907")) {block4 = "900";}
					if(!dao.existBRA(block4, con)) {
						writeOut(fw0, "AML152B" , block0 + "|" + block2 + "|" + block3 + "|" + block4 + "|" + block5 + "|" + block6);
						continue back;}
					fB = new File("D:/dayRpt/" + block4 + "AML152-B.DAT");
					
					outputB = new DataOutputStream(new FileOutputStream(fB, true));		
					long fileBLength = fB.length();
					lineNumberReaderB = new LineNumberReader(new FileReader(fB));
					lineNumberReaderB.skip(fileBLength);
					int lineB = lineNumberReaderB.getLineNumber();
					if(lineB%54 == 0) {
						pageB = (lineB/54) + 1;
						outputB.write(padLeft(changePage + "", 35, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("關注客戶每日異動報表（歸屬單位）", 40, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.write(padLeft("", 37, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("（本報表保存年限為五年）", 40, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.write(padLeft("報表代號：AML152-B", 50, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.write(padLeft("歸屬單位：" + block4, 75, ' ').getBytes("big5"));
						outputB.write(padLeft(year+"年"+month+"月"+date+"日", 41, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("頁次：" + pageB, 4, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.writeUTF("\r\n");
						outputB.write(padLeft("統編", 25, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("關注類型", 22, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("名單資訊", 22, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("備註", 8, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.write(padLeft("戶名", 21, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.writeUTF("\r\n");
						outputB.writeUTF("\r\n");
					}
					outputB.write(padLeft(block0, 49, ' ').getBytes("big5"));
					outputB.write(padLeft("受告誡名單", 38, ' ').getBytes("big5"));
					outputB.write(padLeft("類型："+switchDataType(block6), 18, SBC_SPACE).getBytes("big5"));
					outputB.writeUTF("\r\n");
					outputB.write(padLeft(block1, 46, SBC_SPACE).getBytes("big5"));
					outputB.write(padLeft("告誡起日："+block2, 18, SBC_SPACE).getBytes("big5"));
					outputB.writeUTF("\r\n");
					outputB.write(padLeft("", 46, SBC_SPACE).getBytes("big5"));
					outputB.write(padLeft("告誡迄日："+block3, 18, SBC_SPACE).getBytes("big5"));
					outputB.writeUTF("\r\n");
					outputB.write(padLeft("", 46, SBC_SPACE).getBytes("big5"));
					outputB.write(padLeft("申報警局："+block5, 18, SBC_SPACE).getBytes("big5"));		
					outputB.writeUTF("\r\n");
					outputB.writeUTF("\r\n");
				}		
				back:for(int i=0;i<mapPIdY.size();i++) {
					vo = dao.findByPId(mapPIdY.get(i), con);
					block0 = vo.getcId();
					block1 = vo.getName();
					block2 = vo.getWarningDate();
					block3 = vo.getExpireDate();
					block4 = vo.getRmdBranch();					
					block5 = vo.getIssuer();
					block6 = vo.getDataType();
					block13 = vo.getPidId();
					if(block4.equals("066")) {block4 = "013";}
					if(block4.equals("067")) {block4 = "023";}
					if(block4.equals("050")) {block4 = "026";}
					if(block4.equals("053")) {block4 = "007";}
					if(block4.equals("907")) {block4 = "900";}

					if(!dao.existBRA(block4, con)) {
						writeOut(fw0, "AML152B" , block0 + "|" + block2 + "|" + block3 + "|" + block4 + "|" + block5 + "|" + block6);
						continue back;}
					fB = new File("D:/dayRpt/" + block4 + "AML152-B.DAT");
					
					outputB = new DataOutputStream(new FileOutputStream(fB, true));		
					long fileBLength = fB.length();
					lineNumberReaderB = new LineNumberReader(new FileReader(fB));
					lineNumberReaderB.skip(fileBLength);
					
					int lineB = lineNumberReaderB.getLineNumber();
					if(lineB%54 == 0) {
						pageB = (lineB/54) + 1;
						outputB.write(padLeft(changePage + "", 35, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("關注客戶每日異動報表（歸屬單位）", 40, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.write(padLeft("", 37, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("（本報表保存年限為五年）", 40, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.write(padLeft("報表代號：AML152-B", 50, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.write(padLeft("歸屬單位：" + block4, 75, ' ').getBytes("big5"));
						outputB.write(padLeft(year+"年"+month+"月"+date+"日", 41, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("頁次：" + pageB, 4, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.writeUTF("\r\n");
						outputB.write(padLeft("統編", 25, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("關注類型", 22, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("名單資訊", 22, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("備註", 8, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.write(padLeft("戶名", 21, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.writeUTF("\r\n");
						outputB.writeUTF("\r\n");
					}
					outputB.write(padLeft(block0, 49, ' ').getBytes("big5"));
					outputB.write(padLeft("受告誡名單", 38, ' ').getBytes("big5"));
					outputB.write(padLeft("類型："+switchDataType(block6), 22, SBC_SPACE).getBytes("big5"));		
					outputB.write(padLeft("獨資戶之「負責人統一編號」經警政機關通報為受告", 38, ' ').getBytes("big5"));
					outputB.writeUTF("\r\n");
					outputB.write(padLeft(block1, 46, SBC_SPACE).getBytes("big5"));
					outputB.write(padLeft("告誡起日："+block2, 39, ' ').getBytes("big5"));
					outputB.write(padLeft("誡行為人。", 38, ' ').getBytes("big5"));					
					outputB.writeUTF("\r\n");
					outputB.write(padLeft("命中告誡戶類型：獨資戶負責人", 46, SBC_SPACE).getBytes("big5"));
					outputB.write(padLeft("告誡迄日："+block3, 18, SBC_SPACE).getBytes("big5"));
					outputB.writeUTF("\r\n");
					outputB.write(padLeft("命中告誡戶ID：" + block13, 53, SBC_SPACE).getBytes("big5"));
					outputB.write(padLeft("申報警局："+block5, 18, SBC_SPACE).getBytes("big5"));		
					outputB.writeUTF("\r\n");
					outputB.writeUTF("\r\n");
				}	
				back:for(int i=0;i<mapJointY.size();i++) {
					vo = dao.findByJoint(mapJointY.get(i), con);
					block0 = vo.getcId();
					block1 = vo.getName();
					block2 = vo.getWarningDate();
					block3 = vo.getExpireDate();
					block4 = vo.getRmdBranch();					
					block5 = vo.getIssuer();
					block6 = vo.getDataType();
					block14 = vo.getJointId();
					
					if(block4.equals("066")) {block4 = "013";}
					if(block4.equals("067")) {block4 = "023";}
					if(block4.equals("050")) {block4 = "026";}
					if(block4.equals("053")) {block4 = "007";}
					if(block4.equals("907")) {block4 = "900";}

					if(!dao.existBRA(block4, con)) {
						writeOut(fw0, "AML152B" , block0 + "|" + block2 + "|" + block3 + "|" + block4 + "|" + block5 + "|" + block6);
						continue back;}
					fB = new File("D:/dayRpt/" + block4 + "AML152-B.DAT");
					
					outputB = new DataOutputStream(new FileOutputStream(fB, true));		
					long fileBLength = fB.length();
					lineNumberReaderB = new LineNumberReader(new FileReader(fB));
					lineNumberReaderB.skip(fileBLength);
					
					int lineB = lineNumberReaderB.getLineNumber();
					if(lineB%54 == 0) {
						pageB = (lineB/54) + 1;
						outputB.write(padLeft(changePage + "", 35, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("關注客戶每日異動報表（歸屬單位）", 40, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.write(padLeft("", 37, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("（本報表保存年限為五年）", 40, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.write(padLeft("報表代號：AML152-B", 50, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.write(padLeft("歸屬單位：" + block4, 75, ' ').getBytes("big5"));
						outputB.write(padLeft(year+"年"+month+"月"+date+"日", 41, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("頁次：" + pageB, 4, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.writeUTF("\r\n");
						outputB.write(padLeft("統編", 25, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("關注類型", 22, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("名單資訊", 22, SBC_SPACE).getBytes("big5"));
						outputB.write(padLeft("備註", 8, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.write(padLeft("戶名", 21, SBC_SPACE).getBytes("big5"));
						outputB.writeUTF("\r\n");
						outputB.writeUTF("\r\n");
						outputB.writeUTF("\r\n");
					}
					outputB.write(padLeft(block0, 49, ' ').getBytes("big5"));
					outputB.write(padLeft("受告誡名單", 38, ' ').getBytes("big5"));
					outputB.write(padLeft("類型："+switchDataType(block6), 22, SBC_SPACE).getBytes("big5"));		
					outputB.write(padLeft("聯名戶之「聯名人統一編號」經警政機關通報為受", 38, ' ').getBytes("big5"));
					outputB.writeUTF("\r\n");
					outputB.write(padLeft(block1, 46, SBC_SPACE).getBytes("big5"));
					outputB.write(padLeft("告誡起日："+block2, 39, ' ').getBytes("big5"));
					outputB.write(padLeft("告誡行為人，須對「聯名戶本身」及「所有」聯名", 38, ' ').getBytes("big5"));					
					outputB.writeUTF("\r\n");
					outputB.write(padLeft("命中告誡戶類型：聯名戶聯名人", 46, SBC_SPACE).getBytes("big5"));
					outputB.write(padLeft("告誡迄日："+block3, 39, ' ').getBytes("big5"));
					outputB.write(padLeft("人執行盡職審查。", 38, ' ').getBytes("big5"));					
					outputB.writeUTF("\r\n");
					outputB.write(padLeft("命中告誡戶ID："+block14, 53, SBC_SPACE).getBytes("big5"));
					outputB.write(padLeft("申報警局："+block5, 18, SBC_SPACE).getBytes("big5"));		
					outputB.writeUTF("\r\n");
					outputB.writeUTF("\r\n");
				}
//				back:for(int i=0;i<mapJointY.size();i++) {
					String line = "";	
					String[] a;
					br = new BufferedReader(new InputStreamReader(new FileInputStream(gcisFile), "UTF-8"));
					back2:while((line = br.readLine()) != null) {
						if(line.trim().equals("")) {continue back2;}
						a = line.split(",");
						String gcisId = a[1].trim() + "A";
						String gcisTypeA = switchGCISA(a[0].trim());
						String gcisTypeB = switchGCISB(a[0].trim());
						if(custDao.existGCIS(gcisId, con)) {
							custVo = custDao.findGCISKYC(gcisId, con);
							block15 = custVo.getCId();
							block16 = custVo.getName();
							block17 = custVo.getRmdBranch();
							if(block17.equals("066")) {block17 = "013";}
							if(block17.equals("067")) {block17 = "023";}
							if(block17.equals("050")) {block17 = "026";}
							if(block17.equals("053")) {block17 = "007";}
							if(block17.equals("907")) {block17 = "900";}
							if(block17.equals("801")) {
								writeOut(fw0, "AML152B" , block15 + "|" + block16 + "|" + block17 + "|非營業中名單");
								continue back2;
							}
							/*if(!dao.existBRA(block4, con)) {
								writeOut(fw0, "AML152B" , block0 + "|" + block2 + "|" + block3 + "|" + block4 + "|" + block5 + "|" + block6);
								continue back;}*/
							fB = new File("D:/dayRpt/" + block17 + "AML152-B.DAT");
							
							outputB = new DataOutputStream(new FileOutputStream(fB, true));		
							long fileBLength = fB.length();
							lineNumberReaderB = new LineNumberReader(new FileReader(fB));
							lineNumberReaderB.skip(fileBLength);
							
							int lineB = lineNumberReaderB.getLineNumber();
							if(lineB%54 == 0) {
								pageB = (lineB/54) + 1;
								outputB.write(padLeft(changePage + "", 35, SBC_SPACE).getBytes("big5"));
								outputB.write(padLeft("關注客戶每日異動報表（歸屬單位）", 40, SBC_SPACE).getBytes("big5"));
								outputB.writeUTF("\r\n");
								outputB.write(padLeft("", 37, SBC_SPACE).getBytes("big5"));
								outputB.write(padLeft("（本報表保存年限為五年）", 40, SBC_SPACE).getBytes("big5"));
								outputB.writeUTF("\r\n");
								outputB.write(padLeft("報表代號：AML152-B", 50, SBC_SPACE).getBytes("big5"));
								outputB.writeUTF("\r\n");
								outputB.write(padLeft("歸屬單位：" + block17, 75, ' ').getBytes("big5"));
								outputB.write(padLeft(year+"年"+month+"月"+date+"日", 41, SBC_SPACE).getBytes("big5"));
								outputB.write(padLeft("頁次：" + pageB, 4, SBC_SPACE).getBytes("big5"));
								outputB.writeUTF("\r\n");
								outputB.writeUTF("\r\n");
								outputB.write(padLeft("統編", 25, SBC_SPACE).getBytes("big5"));
								outputB.write(padLeft("關注類型", 22, SBC_SPACE).getBytes("big5"));
								outputB.write(padLeft("名單資訊", 22, SBC_SPACE).getBytes("big5"));
								outputB.write(padLeft("備註", 8, SBC_SPACE).getBytes("big5"));
								outputB.writeUTF("\r\n");
								outputB.write(padLeft("戶名", 21, SBC_SPACE).getBytes("big5"));
								outputB.writeUTF("\r\n");
								outputB.writeUTF("\r\n");
								outputB.writeUTF("\r\n");
							}
							outputB.write(padLeft(block15, 49, ' ').getBytes("big5"));
							outputB.write(padLeft("非營業中名單", 37, ' ').getBytes("big5"));
							outputB.write(padLeft("類型："+gcisTypeA, 22, SBC_SPACE).getBytes("big5"));		
							outputB.write(padLeft("□ 已於__年__月__日通知客戶結清銷戶", 38, ' ').getBytes("big5"));
							outputB.writeUTF("\r\n");
							outputB.write(padLeft(block16, 46, SBC_SPACE).getBytes("big5"));
							outputB.write(padLeft("異動日期："+yesterDate, 39, ' ').getBytes("big5"));
							outputB.write(padLeft("□ 確認帳戶已設為風險帳戶", 38, ' ').getBytes("big5"));					
							outputB.writeUTF("\r\n");
							outputB.write(padLeft("", 46, SBC_SPACE).getBytes("big5"));
							outputB.write(padLeft("異動狀態："+gcisTypeB, 37, ' ').getBytes("big5"));
							outputB.write(padLeft("□ 帳戶已有其他事故：＿＿＿", 38, ' ').getBytes("big5"));					
							outputB.writeUTF("\r\n");
							outputB.write(padLeft("", 53, SBC_SPACE).getBytes("big5"));
							outputB.write(padLeft("", 15, SBC_SPACE).getBytes("big5"));
							outputB.write(padLeft("□ 檢視交易是否申報SAR", 38, ' ').getBytes("big5"));	
							outputB.writeUTF("\r\n");
							outputB.writeUTF("\r\n");
						}						
					}
//				}
		} catch (IOException e) {				
			e.printStackTrace();
		}
		try {
			for(int j=0;j<mapBranchC.size();j++) {
				fAllC = new File("D:/dayRpt/" + mapBranchC.get(j) + "AML152-C.DAT");
				fAllC.createNewFile();
				outputAllC = new DataOutputStream(new FileOutputStream(fAllC, true));
				outputAllC.write(padLeft(changePage + "", 35, SBC_SPACE).getBytes("big5"));
				outputAllC.write(padLeft("關注客戶每日異動報表（總行管理單位）", 40, SBC_SPACE).getBytes("big5"));
				outputAllC.writeUTF("\r\n");
				outputAllC.write(padLeft("", 37, SBC_SPACE).getBytes("big5"));
				outputAllC.write(padLeft("（本報表保存年限為五年）", 40, SBC_SPACE).getBytes("big5"));
				outputAllC.writeUTF("\r\n");
				outputAllC.write(padLeft("報表代號：AML152-C", 50, SBC_SPACE).getBytes("big5"));
				outputAllC.writeUTF("\r\n");
				outputAllC.write(padLeft("總行管理單位：" + mapBranchC.get(j), 73, ' ').getBytes("big5"));
				outputAllC.write(padLeft(year+"年"+month+"月"+date+"日", 41, SBC_SPACE).getBytes("big5"));
				outputAllC.write(padLeft("頁次：1", 4, SBC_SPACE).getBytes("big5"));
				outputAllC.writeUTF("\r\n");
				outputAllC.writeUTF("\r\n");
				outputAllC.write(padLeft("統編", 14, SBC_SPACE).getBytes("big5"));
				outputAllC.write(padLeft("關注類型", 12, SBC_SPACE).getBytes("big5"));
				outputAllC.write(padLeft("AML-KYC已建立業務關係（僅列出貴單位所轄業務）", 32, SBC_SPACE).getBytes("big5"));
				outputAllC.write(padLeft("名單資訊", 23, SBC_SPACE).getBytes("big5"));
				outputAllC.write(padLeft("備註", 1, SBC_SPACE).getBytes("big5"));
				outputAllC.writeUTF("\r\n");
				outputAllC.write(padLeft("戶名", 21, SBC_SPACE).getBytes("big5"));
				outputAllC.writeUTF("\r\n");
				outputAllC.writeUTF("\r\n");
				outputAllC.writeUTF("\r\n");
			}
			back:for(int i=0;i<mapGray.size();i++) {
				thretRISVo = thretRISDao.findById(mapGray.get(i), con);
				block8 = thretRISVo.getThret_Id_No();
				block9 = thretRISVo.getName();
//				block10 = thretRISVo.getAccBranch();
				block11 = thretRISVo.getBrFlag();
				block12 = thretRISVo.getDailyDate();

				if(block11.equals("")) {continue back;}
				for(int k=0;k<block11.split(",").length;k++) {
					fC = new File("D:/dayRpt/" + switchBRFlag(block11.split(",")[k]) + "AML152-C.DAT");
					outputC = new DataOutputStream(new FileOutputStream(fC, true));
					long fileCLength = fC.length();
					lineNumberReaderC = new LineNumberReader(new FileReader(fC));
					lineNumberReaderC.skip(fileCLength);
					
					int lineC = lineNumberReaderC.getLineNumber();
					if(lineC%54 == 0) {
						pageC = (lineC/54) + 1;
						outputC.write(padLeft(changePage + "", 34, SBC_SPACE).getBytes("big5"));
						outputC.write(padLeft("關注客戶每日異動報表（總行管理單位）", 40, SBC_SPACE).getBytes("big5"));
						outputC.writeUTF("\r\n");
						outputC.write(padLeft("", 37, SBC_SPACE).getBytes("big5"));
						outputC.write(padLeft("（本報表保存年限為五年）", 40, SBC_SPACE).getBytes("big5"));
						outputC.writeUTF("\r\n");
						outputC.write(padLeft("報表代號：AML152-C", 50, SBC_SPACE).getBytes("big5"));
						outputC.writeUTF("\r\n");
						outputC.write(padLeft("總行管理單位：" + switchBRFlag(block11.split(",")[k]), 73, ' ').getBytes("big5"));
						outputC.write(padLeft(year+"年"+month+"月"+date+"日", 41, SBC_SPACE).getBytes("big5"));
						outputC.write(padLeft("頁次："+pageC, 4, SBC_SPACE).getBytes("big5"));
						outputC.writeUTF("\r\n");
						outputC.writeUTF("\r\n");
						outputC.write(padLeft("統編", 14, SBC_SPACE).getBytes("big5"));
						outputC.write(padLeft("關注類型", 12, SBC_SPACE).getBytes("big5"));
						outputC.write(padLeft("AML-KYC已建立業務關係（僅列出貴單位所轄業務）", 32, SBC_SPACE).getBytes("big5"));
						outputC.write(padLeft("名單資訊", 23, SBC_SPACE).getBytes("big5"));
						outputC.write(padLeft("備註", 1, SBC_SPACE).getBytes("big5"));
						outputC.writeUTF("\r\n");
						outputC.write(padLeft("戶名", 21, SBC_SPACE).getBytes("big5"));
						outputC.writeUTF("\r\n");
						outputC.writeUTF("\r\n");
						outputC.writeUTF("\r\n");
					}
					outputC.write(padLeft(block8, 27, ' ').getBytes("big5"));
					outputC.write(padLeft("灰名單", 34, ' ').getBytes("big5"));
					outputC.write(padLeft(chgBRFlag(block11.split(",")[k]), 20, SBC_SPACE).getBytes("big5"));
					outputC.write(padLeft("類型：列入灰名單", 18, SBC_SPACE).getBytes("big5"));
					outputC.writeUTF("\r\n");
					outputC.write(padLeft(block9, 52, SBC_SPACE).getBytes("big5"));
					outputC.write(padLeft("列入日期："+block12, 18, SBC_SPACE).getBytes("big5"));
					outputC.writeUTF("\r\n");
					outputC.write(padLeft("", 52, SBC_SPACE).getBytes("big5"));
					outputC.write(padLeft("", 18, SBC_SPACE).getBytes("big5"));
					outputC.writeUTF("\r\n");
					outputC.write(padLeft("", 52, SBC_SPACE).getBytes("big5"));
					outputC.write(padLeft("", 18, SBC_SPACE).getBytes("big5"));
					outputC.writeUTF("\r\n");
					outputC.writeUTF("\r\n");
				}
			}
				back:for(int i=0;i<map.size();i++) {
					vo = dao.findById(map.get(i), con);
					block0 = vo.getcId();
					block1 = vo.getName();
					block2 = vo.getWarningDate();
					block3 = vo.getExpireDate();
//					block4 = vo.getRmdBranch();					
					block5 = vo.getIssuer();
					block6 = vo.getDataType();
					block7 = vo.getBrFlag();
					if(block7.equals("")) {continue back;}
					for(int k=0;k<block7.split(",").length;k++) {
						fC = new File("D:/dayRpt/" + switchBRFlag(block7.split(",")[k]) + "AML152-C.DAT");
						outputC = new DataOutputStream(new FileOutputStream(fC, true));
						long fileCLength = fC.length();
						lineNumberReaderC = new LineNumberReader(new FileReader(fC));
						lineNumberReaderC.skip(fileCLength);
						int lineC = lineNumberReaderC.getLineNumber();
						if(lineC%54 == 0) {
							pageC = (lineC/54) + 1;
							outputC.write(padLeft(changePage + "", 34, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("關注客戶每日異動報表（總行管理單位）", 40, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.write(padLeft("", 37, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("（本報表保存年限為五年）", 40, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.write(padLeft("報表代號：AML152-C", 50, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.write(padLeft("總行管理單位：" + switchBRFlag(block7.split(",")[k]), 73, ' ').getBytes("big5"));
							outputC.write(padLeft(year+"年"+month+"月"+date+"日", 41, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("頁次："+pageC, 4, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.writeUTF("\r\n");
							outputC.write(padLeft("統編", 14, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("關注類型", 12, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("AML-KYC已建立業務關係（僅列出貴單位所轄業務）", 32, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("名單資訊", 23, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("備註", 1, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.write(padLeft("戶名", 21, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.writeUTF("\r\n");
							outputC.writeUTF("\r\n");
						}
							outputC.write(padLeft(block0, 27, ' ').getBytes("big5"));
							outputC.write(padLeft("受告誡名單", 32, ' ').getBytes("big5"));
							outputC.write(padLeft(chgBRFlag(block7.split(",")[k]), 20, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("類型："+switchDataType(block6), 18, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.write(padLeft(block1, 52, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("告誡起日："+block2, 18, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.write(padLeft("", 52, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("告誡迄日："+block3, 18, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.write(padLeft("", 52, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("申報警局："+block5, 18, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.writeUTF("\r\n");
					}
				}
				back:for(int i=0;i<mapPIdY.size();i++) {
					vo = dao.findByPId(mapPIdY.get(i), con);
					block0 = vo.getcId();
					block1 = vo.getName();
					block2 = vo.getWarningDate();
					block3 = vo.getExpireDate();
					block4 = vo.getRmdBranch();					
					block5 = vo.getIssuer();
					block6 = vo.getDataType();
					block7 = vo.getBrFlag();
					block13 = vo.getPidId();
					if(block7.equals("")) {continue back;}
					for(int k=0;k<block7.split(",").length;k++) {
						fC = new File("D:/dayRpt/" + switchBRFlag(block7.split(",")[k]) + "AML152-C.DAT");
						outputC = new DataOutputStream(new FileOutputStream(fC, true));
						long fileCLength = fC.length();
						lineNumberReaderC = new LineNumberReader(new FileReader(fC));
						lineNumberReaderC.skip(fileCLength);
					
						int lineC = lineNumberReaderC.getLineNumber();
						if(lineC%54 == 0) {
							pageC = (lineC/54) + 1;
							outputC.write(padLeft(changePage + "", 34, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("關注客戶每日異動報表（總行管理單位）", 40, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.write(padLeft("", 37, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("（本報表保存年限為五年）", 40, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.write(padLeft("報表代號：AML152-C", 50, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.write(padLeft("總行管理單位：" + switchBRFlag(block7.split(",")[k]), 73, ' ').getBytes("big5"));
							outputC.write(padLeft(year+"年"+month+"月"+date+"日", 41, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("頁次："+pageC, 4, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.writeUTF("\r\n");
							outputC.write(padLeft("統編", 14, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("關注類型", 12, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("AML-KYC已建立業務關係（僅列出貴單位所轄業務）", 32, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("名單資訊", 23, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("備註", 1, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.write(padLeft("戶名", 21, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.writeUTF("\r\n");
							outputC.writeUTF("\r\n");
						}
						outputC.write(padLeft(block0, 27, ' ').getBytes("big5"));
						outputC.write(padLeft("受告誡名單", 32, ' ').getBytes("big5"));
						outputC.write(padLeft(chgBRFlag(block7.split(",")[k]), 20, SBC_SPACE).getBytes("big5"));
						outputC.write(padLeft("類型："+switchDataType(block6), 22, SBC_SPACE).getBytes("big5"));						
						outputC.write(padLeft("獨資戶之「負責人統一編號」經警政機關通報為受告", 18, SBC_SPACE).getBytes("big5"));
						outputC.writeUTF("\r\n");
						outputC.write(padLeft(block1, 52, SBC_SPACE).getBytes("big5"));
						outputC.write(padLeft("告誡起日："+block2, 39, ' ').getBytes("big5"));
						outputC.write(padLeft("誡行為人。", 18, SBC_SPACE).getBytes("big5"));	
						outputC.writeUTF("\r\n");
						outputC.write(padLeft("命中告誡戶類型：獨資戶負責人", 52, SBC_SPACE).getBytes("big5"));
						outputC.write(padLeft("告誡迄日："+block3, 18, ' ').getBytes("big5"));					
						outputC.writeUTF("\r\n");
						outputC.write(padLeft("命中告誡戶ID："+block13, 59, SBC_SPACE).getBytes("big5"));
						outputC.write(padLeft("申報警局："+block5, 18, SBC_SPACE).getBytes("big5"));
						outputC.writeUTF("\r\n");
						outputC.writeUTF("\r\n");
					}
				}
				back:for(int i=0;i<mapJointY.size();i++) {
					vo = dao.findByJoint(mapJointY.get(i), con);
					block0 = vo.getcId();
					block1 = vo.getName();
					block2 = vo.getWarningDate();
					block3 = vo.getExpireDate();
					block4 = vo.getRmdBranch();					
					block5 = vo.getIssuer();
					block6 = vo.getDataType();
					block7 = vo.getBrFlag();
					block14 = vo.getJointId();
					if(block7.equals("")) {continue back;}
					for(int k=0;k<block7.split(",").length;k++) {
						fC = new File("D:/dayRpt/" + switchBRFlag(block7.split(",")[k]) + "AML152-C.DAT");
						outputC = new DataOutputStream(new FileOutputStream(fC, true));
						long fileCLength = fC.length();
						lineNumberReaderC = new LineNumberReader(new FileReader(fC));
						lineNumberReaderC.skip(fileCLength);
					
						int lineC = lineNumberReaderC.getLineNumber();
						if(lineC%54 == 0) {
							pageC = (lineC/54) + 1;
							outputC.write(padLeft(changePage + "", 34, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("關注客戶每日異動報表（總行管理單位）", 40, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.write(padLeft("", 37, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("（本報表保存年限為五年）", 40, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.write(padLeft("報表代號：AML152-C", 50, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.write(padLeft("總行管理單位：" + switchBRFlag(block7.split(",")[k]), 73, ' ').getBytes("big5"));
							outputC.write(padLeft(year+"年"+month+"月"+date+"日", 41, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("頁次："+pageC, 4, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.writeUTF("\r\n");
							outputC.write(padLeft("統編", 14, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("關注類型", 12, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("AML-KYC已建立業務關係（僅列出貴單位所轄業務）", 32, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("名單資訊", 23, SBC_SPACE).getBytes("big5"));
							outputC.write(padLeft("備註", 1, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.write(padLeft("戶名", 21, SBC_SPACE).getBytes("big5"));
							outputC.writeUTF("\r\n");
							outputC.writeUTF("\r\n");
							outputC.writeUTF("\r\n");
						}
						outputC.write(padLeft(block0, 27, ' ').getBytes("big5"));
						outputC.write(padLeft("受告誡名單", 32, ' ').getBytes("big5"));
						outputC.write(padLeft(chgBRFlag(block7.split(",")[k]), 20, SBC_SPACE).getBytes("big5"));
						outputC.write(padLeft("類型："+switchDataType(block6), 22, SBC_SPACE).getBytes("big5"));						
						outputC.write(padLeft("聯名戶之「聯名人統一編號」經警政機關通報為受告", 18, SBC_SPACE).getBytes("big5"));
						outputC.writeUTF("\r\n");
						outputC.write(padLeft(block1, 52, SBC_SPACE).getBytes("big5"));
						outputC.write(padLeft("告誡起日："+block2, 39, ' ').getBytes("big5"));
						outputC.write(padLeft("誡行為人。", 18, SBC_SPACE).getBytes("big5"));						
						outputC.writeUTF("\r\n");
						outputC.write(padLeft("命中告誡戶類型：聯名戶聯名人", 52, SBC_SPACE).getBytes("big5"));
						outputC.write(padLeft("告誡迄日："+block3, 18, SBC_SPACE).getBytes("big5"));
						outputC.writeUTF("\r\n");
						outputC.write(padLeft("命中告誡戶ID："+block14, 59, SBC_SPACE).getBytes("big5"));
						outputC.write(padLeft("申報警局："+block5, 18, SBC_SPACE).getBytes("big5"));
						outputC.writeUTF("\r\n");
						outputC.writeUTF("\r\n");
					}
				}
//				back:for(int i=0;i<mapJointY.size();i++) {
					String line = "";
					String[] a;
					br = new BufferedReader(new InputStreamReader(new FileInputStream(gcisFile), "UTF-8"));
					back:while((line = br.readLine()) != null) {
						if(line.trim().equals("")) {continue back;}
						a = line.split(",");
						String gcisId = a[1].trim() + "A";
						String gcisTypeA = switchGCISA(a[0].trim());
						String gcisTypeB = switchGCISB(a[0].trim());
						if(custDao.existGCIS(gcisId, con)) {
							custVo = custDao.findGCISKYC(gcisId, con);
							block15 = custVo.getCId();
							block16 = custVo.getName();
//							block17 = custVo.getRmdBranch();
							block18 = custVo.getBRFlag();
							if(block18.equals("")) {continue back;}
							for(int k=0;k<block18.split(",").length;k++) {
								fC = new File("D:/dayRpt/" + switchBRFlag(block18.split(",")[k]) + "AML152-C.DAT");
								outputC = new DataOutputStream(new FileOutputStream(fC, true));
								long fileCLength = fC.length();
								lineNumberReaderC = new LineNumberReader(new FileReader(fC));
								lineNumberReaderC.skip(fileCLength);
								
								int lineC = lineNumberReaderC.getLineNumber();
								if(lineC%54 == 0) {
									pageC = (lineC/54) + 1;
									outputC.write(padLeft(changePage + "", 34, SBC_SPACE).getBytes("big5"));
									outputC.write(padLeft("關注客戶每日異動報表（總行管理單位）", 40, SBC_SPACE).getBytes("big5"));
									outputC.writeUTF("\r\n");
									outputC.write(padLeft("", 37, SBC_SPACE).getBytes("big5"));
									outputC.write(padLeft("（本報表保存年限為五年）", 40, SBC_SPACE).getBytes("big5"));
									outputC.writeUTF("\r\n");
									outputC.write(padLeft("報表代號：AML152-C", 50, SBC_SPACE).getBytes("big5"));
									outputC.writeUTF("\r\n");
									outputC.write(padLeft("總行管理單位：" + switchBRFlag(block18.split(",")[k]), 73, ' ').getBytes("big5"));
									outputC.write(padLeft(year+"年"+month+"月"+date+"日", 41, SBC_SPACE).getBytes("big5"));
									outputC.write(padLeft("頁次："+pageC, 4, SBC_SPACE).getBytes("big5"));
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
									outputC.write(padLeft("統編", 14, SBC_SPACE).getBytes("big5"));
									outputC.write(padLeft("關注類型", 12, SBC_SPACE).getBytes("big5"));
									outputC.write(padLeft("AML-KYC已建立業務關係（僅列出貴單位所轄業務）", 32, SBC_SPACE).getBytes("big5"));
									outputC.write(padLeft("名單資訊", 23, SBC_SPACE).getBytes("big5"));
									outputC.write(padLeft("備註", 1, SBC_SPACE).getBytes("big5"));
									outputC.writeUTF("\r\n");
									outputC.write(padLeft("戶名", 21, SBC_SPACE).getBytes("big5"));
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
								}
								outputC.write(padLeft(block15, 27, ' ').getBytes("big5"));
								outputC.write(padLeft("非營業中名單", 31, ' ').getBytes("big5"));
								outputC.write(padLeft(chgBRFlag(block18.split(",")[k]), 20, SBC_SPACE).getBytes("big5"));
								outputC.write(padLeft("類型："+gcisTypeA, 22, SBC_SPACE).getBytes("big5"));						
//								outputC.write(padLeft("", 18, SBC_SPACE).getBytes("big5"));
								outputC.writeUTF("\r\n");
								outputC.write(padLeft(block16, 52, SBC_SPACE).getBytes("big5"));
								outputC.write(padLeft("異動日期："+yesterDate, 39, ' ').getBytes("big5"));
//								outputC.write(padLeft("誡行為人。", 18, SBC_SPACE).getBytes("big5"));						
								outputC.writeUTF("\r\n");
								outputC.write(padLeft("", 52, SBC_SPACE).getBytes("big5"));
								outputC.write(padLeft("異動狀態："+gcisTypeB, 18, SBC_SPACE).getBytes("big5"));
								outputC.writeUTF("\r\n");
								outputC.write(padLeft("", 59, SBC_SPACE).getBytes("big5"));
								outputC.write(padLeft("", 18, SBC_SPACE).getBytes("big5"));
								outputC.writeUTF("\r\n");
								outputC.writeUTF("\r\n");
							}
							writeOutMF1(fw00, String.format("%-10s", block15) + ",A,TAXLOST ,19,05", file1);
							writeOutMF2(fw01, String.format("%-10s", block15) + ",A,TAXLOST ,19,05", file2);
						}
					}
				if(path.isDirectory()) {
					String wordB1 = "一、本報表為每一「日曆日」出表，並應每日列印。倘出表日為假日，歸屬單位應於假日之次一營業日補印假日產生之報表。";
					String wordB2 = "二、「應列印報表日」為T日，歸屬單位應於T+2營業日內完成報表內所有客戶之AML152盡職審查作業，未依規於期限內完成者，將依本行「營業單位內部管理考核辦法」及「總行管理單位考核評定要點」規定進行考核。";
					String wordB3 = "三、如涉及疑似洗錢或資恐交易者，應依本行「疑似洗錢或資恐交易審查及申報管理程序」規定通報專責單位。";
					String wordB4 = "四、報表為機密文件，單位主管應指派專人以專卷保管。";
					String wordC1 = "一、本報表為每一「日曆日」出表。";
					String wordC2 = "二、報表為機密文件，倘有印出紙本報表，單位主管應指派專人以專卷保管。";
					File results[] = path.listFiles();
					LineNumberReader lineNumberReaderBB = null;
					LineNumberReader lineNumberReaderCC = null;
					if(results != null) {
						for(int i=0;i<results.length;i++) {
							if(results[i].getName().endsWith("B.DAT")) {
								outputB = new DataOutputStream(new FileOutputStream(results[i], true));
								long fileYYLength = results[i].length();	
								lineNumberReaderBB = new LineNumberReader(new FileReader(results[i]));
								lineNumberReaderBB.skip(fileYYLength);
								int bLines = lineNumberReaderBB.getLineNumber();
								if(((bLines%54) > 41) || ((bLines>=54) && (bLines%54==0))) {
									outputB.write(padLeft(changePage + "", 36, SBC_SPACE).getBytes("big5"));
									outputB.write(padLeft("關注客戶每日異動報表（歸屬單位）", 40, SBC_SPACE).getBytes("big5"));
									outputB.writeUTF("\r\n");
									outputB.write(padLeft("", 37, SBC_SPACE).getBytes("big5"));
									outputB.write(padLeft("（本報表保存年限為五年）", 40, SBC_SPACE).getBytes("big5"));
									outputB.writeUTF("\r\n");
									outputB.write(padLeft("報表代號：AML152-B", 50, SBC_SPACE).getBytes("big5"));
									outputB.writeUTF("\r\n");
									outputB.write(padLeft("歸屬單位：" + results[i].getName().substring(0, 3), 75, ' ').getBytes("big5"));
									outputB.write(padLeft(year+"年"+month+"月"+date+"日", 41, SBC_SPACE).getBytes("big5"));
									outputB.writeUTF("\r\n");
									//
									outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");
									//
									outputB.writeUTF("\r\n");
									outputB.write(padLeft("", 20, SBC_SPACE).getBytes("big5"));
									outputB.write(padLeft("經辦", 20, SBC_SPACE).getBytes("big5"));
									outputB.write(padLeft("洗錢防制督導主管", 30, SBC_SPACE).getBytes("big5"));
									outputB.write(padLeft("單位主管", 4, SBC_SPACE).getBytes("big5"));
									outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");
									outputB.write(padLeft("註：", 20, SBC_SPACE).getBytes("big5"));
									outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");
									outputB.write(padLeft(wordB1, 58, SBC_SPACE).getBytes("big5"));
									outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");
									outputB.write(padLeft(wordB2, 58, SBC_SPACE).getBytes("big5"));
									outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");
									outputB.write(padLeft(wordB3, 58, SBC_SPACE).getBytes("big5"));
									outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");
									outputB.write(padLeft(wordB4, 58, SBC_SPACE).getBytes("big5"));
									outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");
								} else {
									while((bLines%54) < 41) {
										outputB.writeUTF("\r\n");
										bLines++;
									}
									outputB.writeUTF("\r\n");
									outputB.write(padLeft("", 20, SBC_SPACE).getBytes("big5"));
									outputB.write(padLeft("經辦", 20, SBC_SPACE).getBytes("big5"));
									outputB.write(padLeft("洗錢防制督導主管", 30, SBC_SPACE).getBytes("big5"));
									outputB.write(padLeft("單位主管", 4, SBC_SPACE).getBytes("big5"));
									outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");
									outputB.write(padLeft("註：", 20, SBC_SPACE).getBytes("big5"));
									outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");
									outputB.write(padLeft(wordB1, 58, SBC_SPACE).getBytes("big5"));
									outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");
									outputB.write(padLeft(wordB2, 58, SBC_SPACE).getBytes("big5"));
									outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");
									outputB.write(padLeft(wordB3, 58, SBC_SPACE).getBytes("big5"));
									outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");
									outputB.write(padLeft(wordB4, 58, SBC_SPACE).getBytes("big5"));
									outputB.writeUTF("\r\n");
									outputB.writeUTF("\r\n");
								}
							}
						}
						for(int i=0;i<results.length;i++) {
							if(results[i].getName().endsWith("C.DAT")) {
								outputC = new DataOutputStream(new FileOutputStream(results[i], true));
								long fileCCLength = results[i].length();				
								lineNumberReaderCC = new LineNumberReader(new FileReader(results[i]));
								lineNumberReaderCC.skip(fileCCLength);
								int cLines = lineNumberReaderCC.getLineNumber();
								if(((cLines%54) > 41) || ((cLines>=54) && (cLines%54==0))) {
									outputC.write(padLeft(changePage + "", 36, SBC_SPACE).getBytes("big5"));
									outputC.write(padLeft("關注客戶每日異動報表（總行管理單位）", 40, SBC_SPACE).getBytes("big5"));
									outputC.writeUTF("\r\n");
									outputC.write(padLeft("", 37, SBC_SPACE).getBytes("big5"));
									outputC.write(padLeft("（本報表保存年限為五年）", 40, SBC_SPACE).getBytes("big5"));
									outputC.writeUTF("\r\n");
									outputC.write(padLeft("報表代號：AML152-C", 50, SBC_SPACE).getBytes("big5"));
									outputC.writeUTF("\r\n");
									outputC.write(padLeft("總行管理單位：" + results[i].getName().substring(0, 3), 73, ' ').getBytes("big5"));
									outputC.write(padLeft(year+"年"+month+"月"+date+"日", 41, SBC_SPACE).getBytes("big5"));
									outputC.writeUTF("\r\n");
									//
									outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");outputC.writeUTF("\r\n");
									//
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
									outputC.write(padLeft("註：", 20, SBC_SPACE).getBytes("big5"));
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
									outputC.write(padLeft(wordC1, 58, SBC_SPACE).getBytes("big5"));
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
									outputC.write(padLeft(wordC2, 58, SBC_SPACE).getBytes("big5"));
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
								} else {
									while((cLines%54) <= 41) {
										outputC.writeUTF("\r\n");
										cLines++;
									}
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
									outputC.write(padLeft("註：", 20, SBC_SPACE).getBytes("big5"));
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
									outputC.write(padLeft(wordC1, 58, SBC_SPACE).getBytes("big5"));
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
									outputC.write(padLeft(wordC2, 58, SBC_SPACE).getBytes("big5"));
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
									outputC.writeUTF("\r\n");
								}
							}
						}
					}
				}
		} catch (IOException e) {				
			e.printStackTrace();
		} finally {
			if(!file1.exists()) {
				Path pathFile = Paths.get("D:/KYC/FTP/LocalUser/fukycmf1/out/TAXLOST.txt");
				try {
					Files.createFile(pathFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			File file = new File("D:/RPT_152/AML152B"+(cd.getDate().replaceAll("-", "")));
			if(!file.exists()) {file.mkdir();}
//			File file = new File("D:/RPT_CDD/CDD");
//			filesToAdd.add(file);
			String targetZip = "D:/RPT_152/AML152B" + (cd.getDate().replaceAll("-", "")) + ".zip";
			String password = "109";
			AddFilesWithAESEncryption(targetZip, file, password);			
			
			System.out.println("done!");
			addLineMsg("結束時間：" + getDateTimeMicroSec());
			infoAP(mailto, targetZip);
		}		
	}
	
	private String switchGCISA(String str) {
		String result = "";
		switch (str) {
		case "GCISAPI57":
			result = "前一日公司狀態異動";
			break;
		case "GCISAPI58":
			result = "前一日分公司狀態異動";
			break;
		case "GCISAPI59":
			result = "前一日商業狀態異動";
			break;
		case "GCISAPI60":
			result = "前一日有限合夥狀態異動";
			break;
		}
		return result;
	}
	
	private String switchGCISB(String str) {
		String result = "";
		switch (str) {
		case "GCISAPI57":
		case "GCISAPI60":
			result = "解散";
			break;
		case "GCISAPI58":
			result = "廢止";
			break;
		case "GCISAPI59":
			result = "歇業";
			break;
		}
		return result;
	}

	public void mergeRptB() {
		File path = new File("D:/dayRpt");
		File iFile[] = path.listFiles();
		String oFile = "D:/dayRpt/AML152-B.DAT";
		File file = new File(oFile);
		File moveFileB = new File(path + "/" + cd.getDate().replaceAll("-", "") + "/AML152-B.DAT");
		File dFile = new File(path + "/" + cd.getDate().replaceAll("-", ""));
		if(!dFile.exists()) {dFile.mkdir();}
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(file);
			WritableByteChannel targetChannel = output.getChannel();
			for(int i=0; i<iFile.length;i++) {
				if(iFile[i].getName().endsWith("AML152-B.DAT")) {
					FileInputStream input = new FileInputStream(iFile[i]);
					FileChannel inputChannel = input.getChannel();
					inputChannel.transferTo(0, inputChannel.size(), targetChannel);
					inputChannel.close();
					input.close();
				}
			}
			targetChannel.close();
			output.close();
			System.out.println("移檔成功與否："+file.renameTo(moveFileB));
			System.out.println("done!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void mergeRptC() {
		File path = new File("D:/dayRpt");
		File iFile[] = path.listFiles();
		String oFile = "D:/dayRpt/AML152-C.DAT";
		File file = new File(oFile);
		File moveFileC = new File(path + "/" + cd.getDate().replaceAll("-", "") + "/AML152-C.DAT");
		File dFile = new File(path + "/" + cd.getDate().replaceAll("-", ""));
		if(!dFile.exists()) {dFile.mkdir();}
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(file);
			WritableByteChannel targetChannel = output.getChannel();
			for(int i=0; i<iFile.length;i++) {
				if(iFile[i].getName().endsWith("AML152-C.DAT")) {
					FileInputStream input = new FileInputStream(iFile[i]);
					FileChannel inputChannel = input.getChannel();
					inputChannel.transferTo(0, inputChannel.size(), targetChannel);
					inputChannel.close();
					input.close();
				}
			}
			targetChannel.close();
			output.close();
			System.out.println("移檔成功與否："+file.renameTo(moveFileC));
			System.out.println("done!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void toETL() {
		con = getConnection();
		mapToETL = dao.toETL(con);
		String path1 = "D:/RPT_152/";
		String path2 = "D:/KYC/FTP/LocalUser/fukycaml/out/CUST_WARN_LIST"+ (cd.getDate().replaceAll("-", "")) + ".txt";
		File file1 = new File(path1 + "CUST_WARN_LIST" + (cd.getDate().replaceAll("-", "")) + ".txt");
		File file2 = new File(path2);
		Writer err = null;
		try {
			err = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {			
			e.printStackTrace();
		} finally {
			try {
				err.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for(String i : mapToETL.keySet()) {
			writeOut(fw00, mapToETL.get(i), file1);
			writeOut(fw01, mapToETL.get(i), file2);
		}
	}
	
	public void toETLThret() {
		con = getConnection();
		mapToETLThret = dao.toETLThret(con);
		String path1 = "D:/RPT_152/";
		String path2 = "D:/KYC/FTP/LocalUser/fukycaml/out/CUST_THRET_LIST"+ (cd.getDate().replaceAll("-", "")) + ".txt";
//		Path paths1 = Paths.get(path1 + "CUST_WARN_LIST.txt");
//		Path paths2 = Paths.get(path2);
		File file1 = new File(path1 + "CUST_THRET_LIST" + (cd.getDate().replaceAll("-", "")) + ".txt");
		File file2 = new File(path2);
		Writer err = null;
		try {
			err = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {			
			e.printStackTrace();
		} finally {
			try {
				err.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for(String i : mapToETLThret.keySet()) {
			writeOut(fw00, mapToETLThret.get(i), file1);
			writeOut(fw01, mapToETLThret.get(i), file2);
		}
	}
	
	public void toETLPID() {
		con = getConnection();
		mapToETLPID = dao.toETLPIDY(con);
		String path1 = "D:/RPT_152/";
		String path2 = "D:/KYC/FTP/LocalUser/fukycaml/out/CUST_WARNPID_LIST"+ (cd.getDate().replaceAll("-", "")) + ".txt";
		File file1 = new File(path1 + "CUST_WARNPID_LIST" + (cd.getDate().replaceAll("-", "")) + ".txt");
		File file2 = new File(path2);
		Writer err = null;
		try {
			err = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {			
			e.printStackTrace();
		} finally {
			try {
				err.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for(String i : mapToETLPID.keySet()) {
			writeOut(fw00, mapToETLPID.get(i), file1);
			writeOut(fw01, mapToETLPID.get(i), file2);
		}
	}
	
	private String switchDataType(String block6) {
		String rs = "";
		switch (block6) {
		case "A":
			rs = "今日新增告誡";
			break;
		case "U":
			rs = "今日告誡資料異動";
			break;
		case "D":
			rs = "今日撤銷告誡";
			break;
		case "E":
			rs = "前一日告誡到期";
			break;
		default:
			break;
		}
		return rs;
	}
	
	private String switchBRFlag(String block7) {
		String rs = "";		
		switch (block7) {
		case "01":
		case "02":
			rs = "103";
			break;
		case "03":
			rs = "014";
			break;
		case "05":
			rs = "202";
			break;
		case "06":
			rs = "900";
			break;
		case "07":
			rs = "903";
			break;
		case "08":
			rs = "900";
			break;
		case "09":
			rs = "108";
			break;
		case "11":
			rs = "300";
			break;
		case "12":
			rs = "008";
			break;
		case "13":
			rs = "905";
			break;
		/*case "14":
		case "15":
			rs = "801";
			break;*/
		case "16":
		case "20":
		case "21":
			rs = "911";
			break;
		case "17":
		case "18":
		case "22":
		case "23":
			rs = "105";
			break;
		case "19":
			rs = "104";
			break;
		default:
			break;
		}
		return rs;
	}
	
	private String chgBRFlag(String block7) {
		String rs = "";		
		switch (block7) {
		case "01":
			rs = "台幣存款";
			break;
		case "02":
			rs = "保管箱";
			break;
		case "03":
			rs = "外匯存款";
			break;
		case "05":
			rs = "企業金融";
			break;
		case "06":
			rs = "消費金融";
			break;
		case "07":
			rs = "車輛貸款";
			break;
		case "08":
			rs = "理財貸款";
			break;
		case "09":
			rs = "票券";
			break;
		case "11":
			rs = "財富管理";
			break;
		case "12":
			rs = "信託";
			break;
		case "13":
			rs = "保險";
			break;
		case "14":
			rs = "證券";
			break;
		case "15":
			rs = "期貨輔助";
			break;
		case "16":
			rs = "信用卡業務";
			break;
		case "17":
			rs = "衍生性商品業務";
			break;
		case "18":
			rs = "外匯保證金";
			break;
		case "19":
			rs = "數位存款帳戶";
			break;
		case "20":
			rs = "信用卡業務-收單業務";
			break;
		case "21":
			rs = "信用卡業務-小額貸款業務";
			break;
		case "22":
			rs = "台幣債券";
			break;
		case "23":
			rs = "外國債券";
			break;
		default:
			break;
		}
		return rs;
	}
	
	private static void writeOut(FileWriter fw, String str, String line) {
		File file = new File("D:/RPT_152/AML152B"+(cd.getDate().replaceAll("-", ""))+"/" + str + (cd.getDate().replaceAll("-", "")) + ".txt");		
		try {
			if(fw == null) {
				fw = new FileWriter(file, true);
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pw = new PrintWriter(fw);
		pw.println(line);
		pw.flush();
		try {
			fw.flush();
			fw.close();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private static void writeOutMF1(FileWriter fw, String line, File file) {	
		try {
			if(fw == null) {
				fw = new FileWriter(file, true);
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pw = new PrintWriter(fw);
		pw.println(line);
		pw.flush();
		try {
			fw.flush();
			fw.close();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private static void writeOutMF2(FileWriter fw, String line, File file) {		
		try {
			if(fw == null) {
				fw = new FileWriter(file, true);
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pw = new PrintWriter(fw);
		pw.println(line);
		pw.flush();
		try {
			fw.flush();
			fw.close();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private static void infoAP(String mailto, String targetZip) {
		MailServiceThread mailt = new MailServiceThread("KYC",mailto, "152B未派名單", msg, targetZip);
		mailt.start();			
	}
	
	private static void writeOut(FileWriter fw, String line, File file) {
//	String path = "D:/" + name + "KYC_CDD.txt";		
	try {			
		if(fw == null) {
			fw = new FileWriter(file, true);
		}			
	} catch (IOException e) {
		e.printStackTrace();
	}
	PrintWriter pw = new PrintWriter(fw);
	pw.println(line);
	pw.flush();
	try {
		fw.flush();
		fw.close();
		pw.close();
	} catch (IOException e) {
		e.printStackTrace();
	} 
}
	
	private static void AddFilesWithAESEncryption(String targetZip,File file,String password) {    
        try{
            ZipFile zipFile = new ZipFile(targetZip);
//            zipFile.addFolder(path, parameters);
            
            ZipParameters parameters =new ZipParameters();
            /*parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
              
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL); */
            parameters.setEncryptFiles(true);
              
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
              
  
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            parameters.setPassword(password);
//            zipFile.addFiles(filesToAdd, parameters);
            zipFile.addFolder(file, parameters);
        } catch (ZipException e) {
			e.printStackTrace();
		}
    }
	
	private static void addLineMsg(String str) {
		System.out.println(str);
		msg = msg + str + "\n";		
	}
	
	private String getDateTimeMicroSec() {
		CurrentDate date = new CurrentDate();
		return date.getDateTimeMicroSec();
	}

	private String padLeft(String src, int len, char ch) {
		int diff = len - src.length();
		if(diff <= 0) {
			return src;
		}
		char[] charr = new char[len];
		System.arraycopy(src.toCharArray(), 0, charr, 0, src.length());
		for(int i = src.length(); i < len; i++) {
			charr[i] = ch;
		}
		return new String(charr);
	}
	
	private static Connection getConnection() {
		return DbUtil.getConnection();
	}
}
