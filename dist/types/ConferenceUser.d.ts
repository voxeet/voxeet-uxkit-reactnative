export default class ConferenceUser {
    userId: string | undefined;
    conferenceStatus: string | undefined;
    externalId: string | undefined;
    name: string;
    avatarUrl: string | undefined;
    constructor(externalId: string, name: string, avatarUrl: string | undefined);
}
