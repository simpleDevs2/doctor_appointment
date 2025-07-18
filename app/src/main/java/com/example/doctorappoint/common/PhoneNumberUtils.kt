package com.example.doctorappoint.common

object PhoneNumberUtils {
    
    /**
     * Validates Vietnamese phone number format
     * @param phoneNumber Phone number to validate (should be 10 digits starting with 0)
     * @return true if valid, false otherwise
     */
    fun isValidVietnamesePhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.length == 10 && 
               phoneNumber.startsWith("0") && 
               phoneNumber.matches(Regex("\\d{10}"))
    }
    
    /**
     * Converts Vietnamese phone number to international format for SMS
     * @param phoneNumber Vietnamese phone number (e.g., "0946886575")
     * @return International format (e.g., "+84946886575")
     */
    fun toInternationalFormat(phoneNumber: String): String {
        return if (phoneNumber.startsWith("0")) {
            "+84" + phoneNumber.substring(1)
        } else {
            phoneNumber
        }
    }
    
    /**
     * Converts international format back to Vietnamese format
     * @param internationalPhone International phone number (e.g., "+84946886575")
     * @return Vietnamese format (e.g., "0946886575")
     */
    fun toVietnameseFormat(internationalPhone: String): String {
        return if (internationalPhone.startsWith("+84")) {
            "0" + internationalPhone.substring(3)
        } else {
            internationalPhone
        }
    }
    
    /**
     * Formats phone number for display
     * @param phoneNumber Phone number to format
     * @return Formatted phone number (e.g., "0946 886 575")
     */
    fun formatForDisplay(phoneNumber: String): String {
        val cleanNumber = phoneNumber.replace(Regex("[^\\d]"), "")
        return when {
            cleanNumber.length == 10 && cleanNumber.startsWith("0") -> {
                "${cleanNumber.substring(0, 4)} ${cleanNumber.substring(4, 7)} ${cleanNumber.substring(7)}"
            }
            cleanNumber.length == 11 && cleanNumber.startsWith("84") -> {
                "0${cleanNumber.substring(2, 6)} ${cleanNumber.substring(6, 9)} ${cleanNumber.substring(9)}"
            }
            else -> phoneNumber
        }
    }
} 