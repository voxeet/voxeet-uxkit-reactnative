import ConferenceUser from './ConferenceUser';

export enum UserType {
  USER = "user",
  LISTENER = "listener"
}

export interface JoinUserInfo {
  type?:  UserType;
}

export interface JoinOptions {
  user?: JoinUserInfo;
}