package com.fourshape.a4mcqplus.utils;

public class ContentAccessParams {

    public static final String ACCESS_APP_UTILS = "app_utils",
    ACCESS_CATEGORY_LIST = "categories_list",
    ACCESS_CATEGORY_LIST_WITH_EXAMS = "categories_list_with_exams",
    ACCESS_TAG_LIST = "tags_list",
    ACCESS_EXAM_LIST = "exams_list",
    ACCESS_EXAM_SECTION = "exam_section_list",
    ACCESS_MCQ_EXAM_WISE = "mcqs_exam_wise",
    ACCESS_MCQ_NORMALLY = "mcqs_normally",
    REPORT_MCQ = "report_mcq";
    
    /*
    After Adding PRODUCTION_DOMAIN_ARR, SERVER_URL_ARR, F_ACCESS_USERNAME, F_ACCESS_PASSWORD, FILE_UPLOAD_DIR, this app will start fetching response
    in a normal way.
    */
    

    private final char[] PRODUCTION_DOMAIN_ARR = {};
    private final char[] DEVELOPMENT_DOMAIN_ARR = {'h', 't', 't', 'p', ':', '/', '/', 'l', 'o', 'c', 'a', 'l', 'h', 'o', 's', 't', ':', '8', '0', '8', '0', '/'};

    /*
    URL = 4mcqplus/client/app/v1/says_hello.php
     */

    private static final char[] SERVER_URL_ARR = {};

    private final char[] F_ACCESS_USERNAME = {};
    private final char[] F_ACCESS_PASSWORD = {};

    private final char[] FILE_UPLOAD_DIR = {};

    public String getServerUrl (boolean shouldComputeLiveUrl) {
        if (VariableControls.SHOULD_COMPUTE_LIVE_URL) {
            return String.valueOf(PRODUCTION_DOMAIN_ARR) + String.valueOf(SERVER_URL_ARR);
        } else {
            return String.valueOf(DEVELOPMENT_DOMAIN_ARR) + String.valueOf(SERVER_URL_ARR);
        }
    }

    public String getFileAccessUrl (boolean shouldComputeLiveUrl) {
        if (VariableControls.SHOULD_COMPUTE_LIVE_URL) {
            return String.valueOf(PRODUCTION_DOMAIN_ARR) + String.valueOf(FILE_UPLOAD_DIR) + "/";
        } else {
            return String.valueOf(DEVELOPMENT_DOMAIN_ARR) + String.valueOf(FILE_UPLOAD_DIR) + "/";
        }
    }

    public static final String ACTION_TYPE = "action_type",
            LIMIT = "limit",
            OFFSET = "offset",
            USERNAME_TITLE = "f_access_uname",
            PASSWORD_TITLE = "f_access_password",
            QUERY_PARAM = "helper_param",
            QUERY_SECOND_PARAM = "second_helper_param",
            MCQ_ID = "mcq_id",
            EMAIL_ADDRESS = "email_address",
            REMARKS = "remarks";


}
