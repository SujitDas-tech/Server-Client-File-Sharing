import java.net.*;
import java.io.*;
import java.util.*;
public class FServerM extends Thread
 {

	public static void main(String[] args) {

		try{
		DatagramPacket rp = null;
		 
		DatagramSocket ss = new DatagramSocket(Integer.parseInt(args[0]));
		System.out.println("Server is up....\n");
		
		int count = Integer.parseInt(args[0])+1 ;
		while(true){
		byte [] rd = new byte[512];
		rp = new DatagramPacket(rd,rd.length);
		ss.receive(rp);
		count++;	
		
	
		FServerM my = new FServerM(rp,count,args);
		my.start();
		}
		
	}
		catch(IOException e){}
		

		}



		public static byte[] CRLF = new byte[] { 0x0d, 0x0a };
		public static byte[] RDT = new byte[] { 0x52, 0x44, 0x54 };
		public static byte[] END = new byte[] { 0x45, 0x4e, 0x44 };

    
		DatagramSocket ss = null;
		String [] args = null;
		int count = 0;
		FileInputStream fis = null;
		DatagramPacket rp, sp;
		byte[] rd, sd,td,id,mymsg;
		String intlength;
		InetAddress ip = null;
		int port = 0;
		int los =0;
		int i =0;
		final int [] frame = new int[10];


    protected boolean moreQuotes = true;

    public FServerM(DatagramPacket rp,int count,String args[]) throws IOException {
		super();
		
		 this.ss = new DatagramSocket(count);
		 this.rp = rp;
		 this.count = count;
		 this.args = args; 


	}
	@Override
    public void run(){
        try {
			long th_id = Thread.currentThread().getId();
			
			if(args.length>2){
				for(i=0;i<args.length-2;i++){
	
				frame[i] =Integer.parseInt(args[i+2]);
				}}
				else{frame[0]=-2;}
			
			int consignment = 0;
			intlength = Integer.toString((consignment));
			String strConsignment;
			String strGreeting;
			int result = 0; // number of bytes read
	 		td = new byte[512];
			//ss = new DatagramSocket(Integer.parseInt(args[0]));
			
			
			rd=new byte[512];
			//rp = new DatagramPacket(rd,rd.length);
			mymsg = new byte[517 + intlength.length()];
			while(true && result != -1)
			{
				rd=new byte[512];
				sd=new byte[512];
				id = new byte[1];
				
				try
				{
					//ss.receive(rp);
					
					
				if(rp != null){
					if(new String(rp.getData()).contains("REQUEST"))
					{
						strConsignment = new String(rp.getData());
						ip = rp.getAddress(); 
						port =rp.getPort();
						String a = strConsignment.trim();
						a = a.substring(7,a.length());
						a = args[1]+a;
						fis = new FileInputStream(a);

						System.out.println(th_id+" - Received request for " + a + " from "  + ip + " port "  + port);
						consignment  = 0;
						ss.setSoTimeout(30);
					}}
					else
					{
						rp = new DatagramPacket(rd,rd.length);
						ss.receive(rp);
						strConsignment = new String(rp.getData());
						
						String cons = strConsignment.trim();
						consignment = Byte.toUnsignedInt(rp.getData()[3]);
						System.out.println(th_id+"- Received ACK " + consignment + "\n");
					}
						intlength = Integer.toString((consignment));
						mymsg  = new byte[517 + intlength.length()];
						
						result = fis.read(sd);
						if(result != -1)
						{
							td = sd;
							
							if(consignment == frame[los])
							{
								System.out.println(th_id+"- Forgot Consignment " + consignment);
								rp = null;
								if(los<2){
								los++;}
								continue;
							}
							if(result < 512)
							{
								sd = new byte[result];
								mymsg = new byte[result + 5 + intlength.length()];
								sd = Arrays.copyOfRange(td,0,result);
								id[0] = (byte) consignment;
								mymsg = concatenateByteArrays(RDT, id, sd,END,CRLF); 
								sp=new DatagramPacket(mymsg,mymsg.length,ip,port);
								ss.send(sp); 
								System.out.println(th_id+"- Sent Consignment #" + consignment + "\n");
							}
							else
							{
								id[0] = (byte) consignment; 
								mymsg = concatenateByteArrays(RDT, id, sd,CRLF); 
								sp=new DatagramPacket(mymsg,mymsg.length,ip,port);
							
								ss.send(sp); 
								
								System.out.println(th_id+"- Sent Consignment #" + consignment + "\n");
							}

							rp=null;
							sp = null;

						}
						else	
							result = -1;
				}
				catch(SocketTimeoutException ex)
				{
					intlength = Integer.toString((consignment));
					mymsg  = new byte[517 + intlength.length()];
					System.out.println(th_id+"- Timeout!");
					id[0] = (byte) consignment;
					if(result < 512)
					{
							sd = Arrays.copyOfRange(td,0,result);
							mymsg = concatenateByteArrays(RDT, id, sd,END,CRLF); 
					}
					else
							mymsg = concatenateByteArrays(RDT, id, td,CRLF);
						sp=new DatagramPacket(mymsg,mymsg.length,ip,port);
						ss.send(sp);
						System.out.println(th_id+"- Sent Consignment #" + (consignment) + "\n");
					
					}
					sp = null;
					rp = null;
					}
					
				}
	
				
		catch (IOException ex) {
			System.out.println(ex.getMessage());}
		
		finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
	

	public static byte[] concatenateByteArrays(byte[] a, byte[] b, byte[] c, byte[] d) {
        byte[] result = new byte[a.length + b.length + c.length + d.length]; 
        System.arraycopy(a, 0, result, 0, a.length); 
        System.arraycopy(b, 0, result, a.length, b.length);
        System.arraycopy(c, 0, result, a.length+b.length, c.length);
        System.arraycopy(d, 0, result, a.length+b.length+c.length, d.length);
        return result;
    }

	public static byte[] concatenateByteArrays(byte[] a, byte[] b, byte[] c, byte[] d, byte[] e) {
        byte[] result = new byte[a.length + b.length + c.length + d.length + e.length]; 
        System.arraycopy(a, 0, result, 0, a.length); 
        System.arraycopy(b, 0, result, a.length, b.length);
        System.arraycopy(c, 0, result, a.length+b.length, c.length);
        System.arraycopy(d, 0, result, a.length+b.length+c.length, d.length);
        System.arraycopy(e, 0, result, a.length+b.length+c.length+d.length, e.length);
        return result;
    }
    

 }