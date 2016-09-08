package incident.timo.com;

import java.io.Serializable;

/**
 * Created by tim on 9/7/2016.
 */
public class IncidentData implements Serializable {
        private String description;
        private String dateTime;
        private String longitude;
        private String latitude;
        private String gangRelated;
        private String address;
        private String shortDesc;
        private double distance;
        private String victimCnt;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }
        public String getGangRelated() {
            return gangRelated;
        }
        public void setGangRelated(String gangRelated) {
            this.gangRelated = gangRelated;
        }
        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
        public String getShortDesc() {
            return shortDesc;
        }
        public void setShortDesc(String shortDesc) {
            this.shortDesc = shortDesc;
        }
        public double getDistance(){
            return distance;
        }
        public void setDistance(double dist){
            distance = dist;
        }
        public String getVictimCnt(){ return victimCnt;}
        public void setVictimCnt(String vCnt){victimCnt = vCnt;}
    }

