import ConferenceUser from './ConferenceUser';
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
export interface JoinResult {
    conferenceId?: string;
    conferenceAlias?: string;
    conferenceUsers?: Array<ConferenceUser>;
}
