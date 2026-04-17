package com.hng.internship.profile_app.response;


import java.util.List;

public record ResponseList(String status, int count, List<com.hng.internship.profile_app.response.Data> data) {
}
