package com.xianglin.fellowvillager.app.model;

/**
 * 扩展消息对象
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-11-30
 */
public class ExpandMessage {

    /*{"fromid":"11629","pl":{"content":"踩踩踩踩"},"appid":"chat","mct":"1448880906083","toid":"11629"}*/
    /*{"appid":"123123","fromid":"123123","mct":"12334234","pl":{"content":"你好"},"toid":"123123123"}*/

    public String fromid ;

    public pl pl;

    public String appid;

    public String mct;

    public String  toid;

    public ExpandMessage(){

    }

    public ExpandMessage(String fromid, pl pl,String appid,String mct,String  toid/*Builder builder*/){
        this.fromid = fromid;
        this.pl = pl;
        this.appid = appid;
        this.mct = mct;
        this.toid = toid;
    }


    public String getFromid() {
        return fromid;
    }

    public ExpandMessage setFromid(String fromid) {
        this.fromid = fromid;
        return this;
    }

    public pl getPl() {
        return pl;
    }

    public ExpandMessage setPl(pl pl) {
        this.pl = pl;
        return this;
    }

    public String getAppid() {
        return appid;
    }

    public ExpandMessage setAppid(String appid) {
        this.appid = appid;
        return this;
    }

    public String getMct() {
        return mct;
    }

    public ExpandMessage setMct(String mct) {
        this.mct = mct;
        return this;
    }

    public String getToid() {
        return toid;
    }

    public ExpandMessage setToid(String toid) {
        this.toid = toid;
        return this;
    }
//    public static class Builder{
//        private long fromid ;
//
//        private pl pl;
//
//        private String appid;
//
//        private long mct;
//
//        private long  toid;
//
//
//
//        public Builder fromid(long fromid){
//            this.fromid = fromid;
//            return this;
//        }
//
//        public Builder pl (pl pl){
//            this.pl = pl;
//            return this;
//        }
//
//        public Builder appid(String appid){
//            this.appid = appid;
//            return this;
//        }
//
//        public Builder mct (long mct){
//            this.mct = mct;
//            return this;
//        }
//
//        public Builder toid(long toid){
//            this.toid = toid;
//            return this;
//        }
//
//        public ExpandMessage build(){
//            return new ExpandMessage(this);
//        }
//
//    }



}
