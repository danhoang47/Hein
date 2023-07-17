package com.hein.entity;


public class Review {
    private String reviewId;
    private String productId;
    private int ratingPoint;
    private String reviewContent;
    private String timestamp;
    private String userId;

    private String userReviewName;

    private String userAvatar;

    private User user;

    public Review(String reviewId, String productId, int ratingPoint, String reviewContent, String timestampp, String userId) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.ratingPoint = ratingPoint;
        this.reviewContent = reviewContent;
        this.timestamp = timestampp;
        this.userId = userId;
    }

    public Review(String productId, int ratingPoint, String reviewContent, String timestampp, String userId) {
        this.productId = productId;
        this.ratingPoint = ratingPoint;
        this.reviewContent = reviewContent;
        this.timestamp = timestampp;
        this.userId = userId;
    }

    public Review(String productId, int ratingPoint, String reviewContent, String timestampp, String userReviewName, String userAvatar) {
        this.productId = productId;
        this.ratingPoint = ratingPoint;
        this.reviewContent = reviewContent;
        this.timestamp = timestampp;
        this.userReviewName = userReviewName;
        this.userAvatar = userAvatar;
    }

    public Review(String productId, int ratingPoint, String reviewContent, String timestamp) {
        this.productId = productId;
        this.ratingPoint = ratingPoint;
        this.reviewContent = reviewContent;
        this.timestamp = timestamp;
    }

    public Review(String productId, int ratingPoint, String reviewContent, String timestampp, String userId, User user) {
        this.productId = productId;
        this.ratingPoint = ratingPoint;
        this.reviewContent = reviewContent;
        this.timestamp = timestampp;
        this.userId = userId;
        this.user = user;
    }

    public Review() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getRatingPoint() {
        return ratingPoint;
    }

    public void setRatingPoint(int ratingPoint) {
        this.ratingPoint = ratingPoint;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserReviewName() {
        return userReviewName;
    }

    public void setUserReviewName(String userReviewName) {
        this.userReviewName = userReviewName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId='" + reviewId + '\'' +
                ", productId='" + productId + '\'' +
                ", ratingPoint=" + ratingPoint +
                ", reviewContent='" + reviewContent + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", userId='" + userId + '\'' +
                ", userReviewName='" + userReviewName + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                ", user=" + user +
                '}';
    }
}
