import {API_BASE_URL, ACCESS_TOKEN} from "../common/constants";

const request = options => {
    const headers = new Headers();

    if (options.setContentType !== false) {
        headers.append("Content-Type", "application/json");
    }

    if (localStorage.getItem(ACCESS_TOKEN)) {
        headers.append(
            "Authorization",
            "Bearer " + localStorage.getItem(ACCESS_TOKEN)
        );
    }

    const defaults = {headers: headers};
    options = Object.assign({}, defaults, options);

    return fetch(options.url, options)
        .then(response => response.json()
            .then(json => {
                if (!response.ok) {
                    return Promise.reject(json);
                }
                return json;
            })
        );
}

export function login(loginRequest) {
    return request({

        url: API_BASE_URL + "/auth/sign-in",
        method: "POST",
        body: JSON.stringify(loginRequest)
    });
}

export function signup(signupRequest) {
    return request({

       url: API_BASE_URL + "/auth/sign-up",
       method: "POST",
       body: JSON.stringify(signupRequest)
    });
}

export function getCurrentUser() {

    if ( !localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({

        url: API_BASE_URL + "/users/me",
        method: "GET"
    });
}

export function getUserProfiles(username) {

    if ( !localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({

       url: API_BASE_URL + "/users/summary/" + username,
       method: "GET"
    });
}

export function getAllUsers() {

    if ( !localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({

       url: API_BASE_URL + "/users/all",
       method: "GET"
    });
}

export function uploadImage(imageRequest) {

    if ( !localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({

        setContentType: false,
        url: API_BASE_URL + "/images/upload",
        method: "POST",
        body: imageRequest
    });
}

export function uploadProfilePicture(uri) {

    if ( !localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({

        url: API_BASE_URL + "/users/me/pictures",
        method: "PUT",
        body: uri
    });
}

export function createPost(postRequest) {

    if ( !localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({

        url: API_BASE_URL + "/posts/create",
        method: "POST",
        body: JSON.stringify(postRequest)
    });
}

export function getCurrentUserPosts() {

    if ( !localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({

       url: API_BASE_URL + "/posts/me",
       method: "GET"
    });
}

export function getUserPosts(username) {

    if ( !localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({

       url: API_BASE_URL + "/posts/" + username,
       method: "GET"
    });
}

export function follow(followRequest) {

    if ( !localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({

        url: API_BASE_URL + "/follow_users/followers",
        method: "POST",
        body: JSON.stringify(followRequest)
    });
}

export function getFollowersAndFollowing(username) {

    if ( !localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({

        url: API_BASE_URL + "/follow_users/" + username + "/degree",
        method: "GET"
    });
}

export function isFollowing(firstUsername, secondUsername) {

    if ( !localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({

        url: API_BASE_URL + "/follow_users/" + firstUsername + "/following" + secondUsername,
        method: "GET"
    });
}

export function getFollowing(username) {

    if ( !localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({

        url: API_BASE_URL + "/follow_users/" + username + "/following",
        method: "GET"
    });
}

export function getFollowers(username) {

    if ( !localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({

        url: API_BASE_URL + "/follow_users/" + username + "/followers",
        method: "GET"
    });
}

export function getFeed(username, pagingState) {

    if ( !localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    let url = API_BASE_URL + "/feed" + username;

    if (pagingState != null) {
        url += "?ps=" + pagingState;
    }

    return request({

        url: url,
        method: "GET"
    });
}