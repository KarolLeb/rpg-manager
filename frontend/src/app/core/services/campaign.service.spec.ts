import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CampaignService } from './campaign.service';
import { Campaign, CreateCampaignRequest } from '../models/campaign.model';

describe('CampaignService', () => {
  let service: CampaignService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CampaignService]
    });
    service = TestBed.inject(CampaignService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve all campaigns', () => {
    const dummyCampaigns: Campaign[] = [
      { id: 1, uuid: 'uuid1', name: 'Campaign 1', description: 'Desc 1', creationDate: '2023-01-01T00:00:00Z', status: 'ACTIVE', gameMasterId: 10, gameMasterName: 'GM1' },
      { id: 2, uuid: 'uuid2', name: 'Campaign 2', description: 'Desc 2', creationDate: '2023-01-02T00:00:00Z', status: 'FINISHED', gameMasterId: 10, gameMasterName: 'GM1' }
    ];

    service.getCampaigns().subscribe(campaigns => {
      expect(campaigns.length).toBe(2);
      expect(campaigns).toEqual(dummyCampaigns);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/campaigns');
    expect(req.request.method).toBe('GET');
    req.flush(dummyCampaigns);
  });

  it('should retrieve a campaign by ID', () => {
    const dummyCampaign: Campaign = { id: 1, uuid: 'uuid1', name: 'Campaign 1', description: 'Desc 1', creationDate: '2023-01-01T00:00:00Z', status: 'ACTIVE', gameMasterId: 10, gameMasterName: 'GM1' };

    service.getCampaign(1).subscribe(campaign => {
      expect(campaign).toEqual(dummyCampaign);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/campaigns/1');
    expect(req.request.method).toBe('GET');
    req.flush(dummyCampaign);
  });

  it('should create a campaign', () => {
    const newCampaignRequest: CreateCampaignRequest = { name: 'New Campaign', description: 'New Desc', gameMasterId: 10 };
    const createdCampaign: Campaign = { id: 1, uuid: 'uuid1', name: 'New Campaign', description: 'New Desc', creationDate: '2023-01-01T00:00:00Z', status: 'ACTIVE', gameMasterId: 10, gameMasterName: 'GM1' };

    service.createCampaign(newCampaignRequest).subscribe(campaign => {
      expect(campaign).toEqual(createdCampaign);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/campaigns');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newCampaignRequest);
    req.flush(createdCampaign);
  });

  it('should update a campaign', () => {
    const updateRequest: CreateCampaignRequest = { name: 'Updated Campaign', description: 'Updated Desc', gameMasterId: 10 };
    const updatedCampaign: Campaign = { id: 1, uuid: 'uuid1', name: 'Updated Campaign', description: 'Updated Desc', creationDate: '2023-01-01T00:00:00Z', status: 'ACTIVE', gameMasterId: 10, gameMasterName: 'GM1' };

    service.updateCampaign(1, updateRequest).subscribe(campaign => {
      expect(campaign).toEqual(updatedCampaign);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/campaigns/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updateRequest);
    req.flush(updatedCampaign);
  });

  it('should delete a campaign', () => {
    service.deleteCampaign(1).subscribe(response => {
      expect(response).toBeNull();
    });

    const req = httpMock.expectOne('http://localhost:8080/api/campaigns/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
