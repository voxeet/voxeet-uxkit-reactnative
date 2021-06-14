export default class ConferenceUser {
    externalId: string | undefined;
    name: string;
    avatarUrl: string | undefined;
    constructor(externalId: string, name: string, avatarUrl: string | undefined);
}
