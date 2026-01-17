package com.vishal.linkedInProject.postsService.auth;

public class AuthContextHolder {
    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    static void setUserId(Long userId) {
        currentUserId.set(userId);
    }

    public static Long getCurrentUserId() {
        return currentUserId.get();
    }

    static void clear() {
        currentUserId.remove();
    }
}
