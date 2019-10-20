import ConferenceUser from './types/ConferenceUser';
import CreateOptions from './types/CreateConference';
import JoinOptions from './types/JoinConference';
export interface RefreshCallback {
    (): void;
}
export interface TokenRefreshCallback {
    (): Promise<string>;
}
export default class _VoxeetSDK {
    refreshAccessTokenCallback: RefreshCallback | null;
    initialize(consumerKey: string, consumerSecret: string): Promise<any>;
    initializeToken(accessToken: string | undefined, refreshToken: TokenRefreshCallback): any;
    connect(userInfo: ConferenceUser): Promise<any>;
    disconnect(): Promise<any>;
    create(options: CreateOptions): Promise<any>;
    join(conferenceId: string, options?: JoinOptions): Promise<any>;
    leave(): Promise<any>;
    invite(conferenceId: string, participants: ConferenceUser[]): Promise<any>;
    sendBroadcastMessage(message: string): Promise<any>;
    isTelecomMode(): Promise<boolean>;
    isAudio3DEnabled(): Promise<boolean>;
    appearMaximized(enable: boolean): boolean;
    defaultBuiltInSpeaker(enable: boolean): boolean;
    defaultVideo(enable: boolean): boolean;
    screenAutoLock(activate: boolean): void;
    isUserLoggedIn(): Promise<boolean>;
    checkForAwaitingConference(): Promise<any>;
    startConference(conferenceId: string, participants: Array<ConferenceUser>): Promise<any>;
    stopConference(): Promise<any>;
    openSession(userInfo: ConferenceUser): Promise<any>;
    closeSession(): Promise<any>;
}
