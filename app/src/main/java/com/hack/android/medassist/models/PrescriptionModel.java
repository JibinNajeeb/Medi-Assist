package com.hack.android.medassist.models;

import com.hack.android.alexa.data.Directive;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionModel
{
    private Prescription[] prescription;

    public Prescription[] getPrescription ()
    {
        return prescription;
    }

    public void setPrescription (Prescription[] prescription)
    {
        this.prescription = prescription;
    }

    public static class Prescription {
        private String model;

        private String pk;

        private Fields fields;

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getPk() {
            return pk;
        }

        public void setPk(String pk) {
            this.pk = pk;
        }

        public Fields getFields ()
        {
            return fields;
        }

        public void setFields (Fields fields)
        {
            this.fields = fields;
        }


        @Override
        public String toString()
        {
            return "ClassPojo [model = "+model+", pk = "+pk+"]";
        }
    }
    public static class Fields
    {
        private String duration;

        private String dosage;

        private String medicine;

        private String frequency;

        public String getDuration ()
        {
            return duration;
        }

        public void setDuration (String duration)
        {
            this.duration = duration;
        }

        public String getDosage ()
        {
            return dosage;
        }

        public void setDosage (String dosage)
        {
            this.dosage = dosage;
        }

        public String getMedicine ()
        {
            return medicine;
        }

        public void setMedicine (String medicine)
        {
            this.medicine = medicine;
        }

        public String getFrequency ()
        {
            return frequency;
        }

        public void setFrequency (String frequency)
        {
            this.frequency = frequency;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [duration = "+duration+", dosage = "+dosage+", medicine = "+medicine+", frequency = "+frequency+"]";
        }
    }
}
