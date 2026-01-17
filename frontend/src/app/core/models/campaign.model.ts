export interface Campaign {
  id: number;
  uuid: string;
  name: string;
  description: string;
  creationDate: string;
  status: 'ACTIVE' | 'FINISHED' | 'ARCHIVED';
  gameMasterId: number;
  gameMasterName: string;
}

export interface CreateCampaignRequest {
  name: string;
  description: string;
  gameMasterId: number;
}
