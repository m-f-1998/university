package uk.co.coryalexander.pedalpay.uk.co.coryalexander.pedalpay.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("pid")
    @Expose
    private int pid;
    @SerializedName("bid")
    @Expose
    private int bid;
    @SerializedName("lid")
    @Expose
    private int lid;
    @SerializedName("bookstart")
    @Expose
    private String bookstart;
    @SerializedName("bookend")
    @Expose
    private String bookend;
    @SerializedName("returned")
    @Expose
    private int returned;
    @SerializedName("processing")
    @Expose
    private int processing;
    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("distance")
    @Expose
    private int distance;

    /**
     * No args constructor for use in serialization
     *
     */
    public Message() {
    }

    /**
     *
     * @param id
     * @param lid
     * @param distance
     * @param bookend
     * @param processing
     * @param pid
     * @param code
     * @param bookstart
     * @param returned
     * @param bid
     */
    public Message(int id, int pid, int bid, int lid, String bookstart, String bookend, int returned, int processing, int code, int distance) {
        super();
        this.id = id;
        this.pid = pid;
        this.bid = bid;
        this.lid = lid;
        this.bookstart = bookstart;
        this.bookend = bookend;
        this.returned = returned;
        this.processing = processing;
        this.code = code;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getLid() {
        return lid;
    }

    public void setLid(int lid) {
        this.lid = lid;
    }

    public String getBookstart() {
        return bookstart;
    }

    public void setBookstart(String bookstart) {
        this.bookstart = bookstart;
    }

    public String getBookend() {
        return bookend;
    }

    public void setBookend(String bookend) {
        this.bookend = bookend;
    }

    public int getReturned() {
        return returned;
    }

    public void setReturned(int returned) {
        this.returned = returned;
    }

    public int getProcessing() {
        return processing;
    }

    public void setProcessing(int processing) {
        this.processing = processing;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

}