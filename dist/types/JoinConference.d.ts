export declare enum UserType {
    USER = "user",
    LISTENER = "listener"
}
export interface JoinUserInfo {
    type?: UserType;
}
export interface JoinOptions {
    user?: JoinUserInfo;
}
