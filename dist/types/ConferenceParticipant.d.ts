export default class ConferenceParticipant {
    externalId: string | undefined;
    name: string;
    avatarUrl: string | undefined;
    constructor(externalId: string | undefined, name: string, avatarUrl: string | undefined);
}
