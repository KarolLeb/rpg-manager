export interface Character {
  id?: number;
  name: string;
  race?: string;
  characterClass: string;
  level: number;
  stats: string;
  campaignId?: number;
  ownerId?: number;
  controllerId?: number;
}
