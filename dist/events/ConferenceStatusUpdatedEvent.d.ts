export declare enum ConferenceStatus {
    DEFAULT = "DEFAULT",
    CREATING = "CREATING",
    CREATED = "CREATED",
    JOINING = "JOINING",
    JOINED = "JOINED",
    /** @deprecated */
    FIRST_PARTICIPANT = "FIRST_PARTICIPANT",
    /** @deprecated */
    NO_MORE_PARTICIPANT = "NO_MORE_PARTICIPANT",
    LEAVING = "LEAVING",
    LEFT = "LEFT",
    ERROR = "ERROR",
    DESTROYED = "DESTROYED",
    ENDED = "ENDED"
}
export interface ConferenceStatusUpdatedEvent {
    conferenceId: string;
    conferenceAlias: string;
    status: ConferenceStatus;
}
