import java.net.*;
import java.io.*;
import java.util.*;
 
public class FClientM {
 
	public static byte[] CRLF = new byte[] { 0x0d, 0x0a };
	public static void main(String[] args) {
	 
	    DatagramSocket cs = null;
		FileOutputStream fos = null;
		
		try 
		{   
			int prt = 0;
			boolean flag = false;
			int ch[] = new int[10];
			cs = new DatagramSocket();
			int count = 0;
			int i,j,k,pos = 0,copy = 0;
			String fname = args[2];
			byte [] msg,id;
			if(args.length>3){
			for(i=0;i<args.length-3;i++){

			ch[i] =Integer.parseInt(args[i+3]);
			}}
			else{ch[0]=-2;}
			int los =0;
			byte[] rd, sd;
			String GREETING = "REQUEST" + fname + "\r\n";
			String reply1 = "";
            String rec = "";
            byte [] reply = new byte[500];
            byte [] new_reply = new byte[500];
			DatagramPacket sp,rp;
			boolean end = false;
			System.out.println("Requesting " + fname + " from " + InetAddress.getByName(args[0]) + " port " + Integer.parseInt(args[1]) + "\n");
			fos = new FileOutputStream(fname.split("\\.")[0] + "1." + fname.split("\\.")[1]);
			int f = 1;
			String ack= "ACK";
			while(!end)
			{   	  
                sd = new byte[3];
				id = new byte[1];
				msg = new byte[612];
				String intlength = Integer.toString(copy);
                rec = "Received Consignment " + (copy);
				String f_ack = "Forgot ACK " + Integer.toString(count+1) + "\nReceived Consignment " + count + " duplicate - Discarding\n" + "Sent ACK " + Integer.toString(count  + 1) + "\n";
				
                
				rd=new byte[517 + intlength.length()];
				rp=new DatagramPacket(rd,rd.length);
				if(count == 0)
				{
					sd=GREETING.getBytes();
					sp=new DatagramPacket(sd,sd.length, 
							InetAddress.getByName(args[0]),
							Integer.parseInt(args[1]));

					cs.send(sp);
				}

				sd = ack.getBytes();
				cs.receive(rp);
						prt = rp.getPort();
						byte [] td = new byte[3];
						reply1 =new String(rp.getData());
						for(i = 1; i < rp.getData().length; i++)
						{
							if(rp.getData()[i-1] == 0x0d && rp.getData()[i] == 0x0a )
							{
								for(j = i - 4,k=0; j <= i-2; j++,k++)
								{
									td[k] = rp.getData()[j];
								}
								if(new String(td).equals("END"))
								{
									pos = i - 5;
									
								}
								else
									pos = i - 2;
							}
							
						}
						id[0] = (byte) ((count + 1));
						
						reply  = Arrays.copyOfRange(rp.getData(),4,pos + 1);
						if (reply1.trim().substring(reply1.trim().length()-3,reply1.trim().length()).equals("END"))
						{ 
							end = true;
							f_ack = "Forgot ACK " + Integer.toString(0) + "\nReceived Consignment " + count + " duplicate - Discarding\n" + "Sent ACK " + Integer.toString(0) + "\n";

							 
						}
			
					if(count == ch[los])
					{
						if(f == 1){
                          
							fos.write(reply);
							
							System.out.println(rec);
							}
							cs.receive(rp);
                            
                            new_reply = Arrays.copyOfRange(rp.getData(),4,pos + 1);
                            
							if(Arrays.equals(reply,new_reply))
							{
								System.out.println(f_ack);
								sd = ack.getBytes();
								if(end == true)
								{
									count = 0;
									id[0] = (byte) count;
								}
								msg = concatenateByteArrays(sd,id,CRLF);
								sp=new DatagramPacket(msg,msg.length, 
							InetAddress.getByName(args[0]),
							prt);

								cs.send(sp);
                                if(end == true)
								{
									try
									{
										Thread.sleep(500);
										//end = true;
									}
									catch(Exception e){} 
								}
								count++;
                                copy = count;
							}
							else
							{
								f = 2;
							}
							if(los<2){
								los++;
							}
							
						}
					else
					{			
						if(end == false)
						{
							fos.write(reply);
							System.out.println(rec);
							sd = ack.getBytes();
							msg = concatenateByteArrays(sd,id,CRLF);
							sp=new DatagramPacket(msg,msg.length, 
									InetAddress.getByName(args[0]),
									prt);
							cs.send(sp);
							count++;
							copy = count;
							System.out.println("Sent ACK " + (count) + "\n");
						}
						else
						{
							
								fos.write(reply);
								System.out.println(rec);
								
									count = 0;
									id[0] = (byte) count;
								msg = concatenateByteArrays(sd,id,CRLF);
								sp=new DatagramPacket(msg,msg.length, 
										InetAddress.getByName(args[0]),
										prt);
								cs.send(sp);
								System.out.println("Sent ACK 0\n");
								try
								{
									Thread.sleep(500);
								}
								catch(Exception e){}
							
							
							
						}
                
					}
				}
            
			System.out.println("END");
			cs.close();

		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		finally {
			try {
			if (fos != null) fos.close();
			if (cs != null) cs.close();
			} catch (IOException ex) { System.out.println(ex.getMessage());} } 
				} 

		public static byte[] concatenateByteArrays(byte[] a, byte[] b, byte[] c) {
        byte[] result = new byte[a.length + b.length + c.length]; 
        System.arraycopy(a, 0, result, 0, a.length); 
        System.arraycopy(b, 0, result, a.length, b.length);
		System.arraycopy(c, 0, result, a.length + b.length, c.length);
        return result;
		}
}
