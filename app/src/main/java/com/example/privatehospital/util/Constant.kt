package com.example.privatehospital.util

const val TAG = "PrivateHospitalLog"

/**
 * This constant is used to
 */
const val SERVICE_TITLE = "service"

/**
 * This constant is used to
 */
const val CATEGORY_TITLE = "category"

/**
 * This constant is used to
 */
const val HOSPITAL_TITLE = "hospital"

/**
 * This constant is used to
 */
const val DOCTOR_TITLE = "doctor"

/**
 * This constant is base url of real-time database in firebase
 */
const val BASE_URL = "https://privatehospital-b48d7-default-rtdb.firebaseio.com"

/**
 * This is regex of password: least 8 letters with least 1 uppercase letter, 1 digit.
 */
const val REGEX_PASSWORD = "^(?=.*\\d)(?=.*[A-Z]).{8,}\$"

/**
 * This is default phone number of VietNam
 */
const val COUNTRY_PHONE_NUMBER = "+84"

/**
 * This is key-references of data store which is used to save account info when user select "remember"
 */
const val ACCOUNT = "account_login"

/**
 * This is key-references of phone number in data store whose key is "account_login"
 */
const val PHONE_NUMBER = "phone_number_login"

/**
 * This is key-references of password in data store whose key is "account_login"
 */
const val PASSWORD = "password_login"

/**
 * This is time zone of VietNam
 */
const val TIME_ZONE = "Asia/Bangkok"

/**
 * This is used to
 */
const val DEFAULT_TIME_CAN_REMOVE = 12

/**
 * This is notification channel id
 */
const val NOTIFICATION_CHANNEL_ID = "hospital_notification_channel_id"

/**
 * This is notification id
 */
const val NOTIFICATION_ID = 0