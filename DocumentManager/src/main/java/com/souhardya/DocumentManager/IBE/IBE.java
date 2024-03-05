package com.souhardya.DocumentManager.IBE;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.security.*;
import java.util.HashMap;
import java.util.Map;

public class IBE {
    //public static Map<Long, ElementBytePair> ciphertextPair = new HashMap<>();
    //private static Map<String, BytePair> SKQID = new HashMap<>();
    private static Map<String, Element> qID = new HashMap<>();
    private Pairing bp;
    private Element x;
    private Element g;
    private Element gx;

    public IBE(){
        this.bp = PairingFactory.getPairing("params/a.properties");
        this.x = this.bp.getZr().newRandomElement().getImmutable();		//masterSecretKey
        this.g = this.bp.getG1().newRandomElement().getImmutable();		//this is actually Q
        this.gx = this.g.powZn(this.x).getImmutable();
    }

    public void setup(String userEmail) throws NoSuchAlgorithmException {

        byte[] idHash = sha1(userEmail);
        Element QID = this.bp.getG1().newElementFromHash(idHash, 0, idHash.length).getImmutable();  // hash(Identity)
        //Element sk = QID.powZn(this.x).getImmutable();
        //SKQID.put(userEmail,new BytePair(QID,sk));
        qID.put(userEmail,QID);
    }
    public Element keyGeneration(Element QID){
        Element sk = QID.powZn(this.x).getImmutable();

        return sk;
    }
    public ElementBytePair encryption(byte[] messageByte,String userMail) throws NoSuchAlgorithmException {
        //encrypt
        //BytePair userPair=SKQID.get(userMail);
        Element QID=qID.get(userMail);
        Element r = this.bp.getZr().newRandomElement().getImmutable();
        Element C1 = this.g.powZn(r).getImmutable();
        Element gID = this.bp.pairing(QID, this.gx).powZn(r).getImmutable();
        String gIDString = new String(gID.toBytes());
        byte[] HgID = sha1(gIDString);

        byte[] C2 = new byte[messageByte.length];

        for (int i = 0; i < messageByte.length; i++){
            C2[i] = (byte)(messageByte[i] ^ HgID[i]);
        }

        return new ElementBytePair(C1,C2);
//        ciphertextPair.put(userMail,new ElementBytePair(C1,C2));
//        return C2;
    }

    public byte[] decryption(Element C1,byte[] C2,String userMail) throws NoSuchAlgorithmException {
        //ElementBytePair userPair = ciphertextPair.get(userMail);
        //Element C1=userPair.getElement();
        // byte[] C2=userPair.getByteArray();
        //BytePair srk=SKQID.get(userMail);
        Element QID=qID.get(userMail);

        //if(QID==null)return C2;
        if(QID==null){
            setup(userMail);
            QID=qID.get(userMail);
        }
        Element sk=keyGeneration(QID);
        //Element sk= srk.getElement2();
        Element zID = this.bp.pairing(sk, C1).getImmutable();
        String zIDString = new String(zID.toBytes());

        byte[] ZgID = sha1(zIDString);
        byte[] res = new byte[C2.length];
        for (int i = 0; i < C2.length; i++) {
            res[i] = (byte) (C2[i] ^ ZgID[i]);
        }
        return res;
    }

    public static byte[] sha1(String content) throws NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance("SHA-1");
        instance.update(content.getBytes());
        return instance.digest();
    }
}

