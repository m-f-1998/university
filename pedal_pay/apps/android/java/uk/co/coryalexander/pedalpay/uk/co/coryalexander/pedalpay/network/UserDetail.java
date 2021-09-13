package uk.co.coryalexander.pedalpay.uk.co.coryalexander.pedalpay.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDetail {

        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("dob")
        @Expose
        private String dob;
        @SerializedName("mobile")
        @Expose
        private String mobile;
        @SerializedName("addressone")
        @Expose
        private String addressone;
        @SerializedName("addresstwo")
        @Expose
        private String addresstwo;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("zip")
        @Expose
        private String zip;
        @SerializedName("gender")
        @Expose
        private String gender;
        @SerializedName("id")
        @Expose
        private int id;

        /**
         * No args constructor for use in serialization
         *
         */
        public UserDetail() {
        }

        /**
         *
         * @param id
         * @param zip
         * @param email
         * @param dob
         * @param name
         * @param gender
         * @param addressone
         * @param city
         * @param addresstwo
         * @param mobile
         */
        public UserDetail(String email, String name, String dob, String mobile, String addressone, String addresstwo, String city, String zip, String gender, int id) {
            super();
            this.email = email;
            this.name = name;
            this.dob = dob;
            this.mobile = mobile;
            this.addressone = addressone;
            this.addresstwo = addresstwo;
            this.city = city;
            this.zip = zip;
            this.gender = gender;
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getAddressone() {
            return addressone;
        }

        public void setAddressone(String addressone) {
            this.addressone = addressone;
        }

        public String getAddresstwo() {
            return addresstwo;
        }

        public void setAddresstwo(String addresstwo) {
            this.addresstwo = addresstwo;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }


    }
