package com.hng.internship.profile_app.response;

import com.hng.internship.profile_app.model.Data;

public record ProfileAlreadyExistsResponse(String status, String message, Data data) {
}
