package com.androideasy.refresh;

public enum REFRESH_STATUS {
    STATUS_REFRESHING,
    STATUS_RELEASE_TO_REFRESH,
    STATUS_SWIPING_TO_REFRESH,
    STATUS_DEFAULT,
    STATUS_SWIPING_TO_LOAD_MORE,
    STATUS_RELEASE_TO_LOAD_MORE,
    STATUS_LOADING_MORE   ;


    public static boolean isRefreshing(REFRESH_STATUS status) {
        return status == REFRESH_STATUS.STATUS_REFRESHING;
    }

    public static boolean isLoadingMore(REFRESH_STATUS status) {
        return status == REFRESH_STATUS.STATUS_LOADING_MORE;
    }

    public static boolean isStatusDefault(REFRESH_STATUS status) {
        return status == REFRESH_STATUS.STATUS_DEFAULT;
    }

    public static boolean isReleaseToRefresh(REFRESH_STATUS status) {
        return status == REFRESH_STATUS.STATUS_RELEASE_TO_REFRESH;
    }

    public static boolean isReleaseToLoadMore(REFRESH_STATUS status) {
        return status == REFRESH_STATUS.STATUS_RELEASE_TO_LOAD_MORE;
    }

    public static boolean isSwipingToRefresh(REFRESH_STATUS status) {
        return status == REFRESH_STATUS.STATUS_SWIPING_TO_REFRESH;
    }

    public static boolean isSwipingToLoadMore(REFRESH_STATUS status) {
        return status == REFRESH_STATUS.STATUS_SWIPING_TO_LOAD_MORE;
    }

    public static boolean isRefreshStatus(REFRESH_STATUS status) {

        switch (status) {
            case STATUS_REFRESHING:
            case STATUS_RELEASE_TO_REFRESH:
            case STATUS_SWIPING_TO_REFRESH:
                return true;
        }
        return false;
    }

    public static boolean isLoadMoreStatus(REFRESH_STATUS status) {
        switch (status) {
            case STATUS_REFRESHING:
            case STATUS_RELEASE_TO_REFRESH:
            case STATUS_SWIPING_TO_REFRESH:
            case STATUS_DEFAULT:
                return false;
        }
        return true;
    }
}
