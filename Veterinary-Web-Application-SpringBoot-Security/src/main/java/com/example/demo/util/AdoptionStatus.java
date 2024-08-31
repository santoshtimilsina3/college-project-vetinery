package com.example.demo.util;

    public enum AdoptionStatus {
        AVAILABLE("Available"),
        ADOPTED("Adopted"),
        PENDING("Pending"),
        UNAVAILABLE("Unavailable");

        private final String displayName;

        AdoptionStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
