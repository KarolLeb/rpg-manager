import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { StyleService } from './style.service';
import { Renderer2, RendererFactory2 } from '@angular/core';

describe('StyleService', () => {
    let service: StyleService;
    let httpMock: HttpTestingController;
    let renderer: jasmine.SpyObj<Renderer2>;

    beforeEach(() => {
        const rendererSpy = jasmine.createSpyObj('Renderer2', ['createElement', 'setProperty', 'appendChild', 'removeChild']);
        const rendererFactorySpy = jasmine.createSpyObj('RendererFactory2', ['createRenderer']);
        rendererFactorySpy.createRenderer.and.returnValue(rendererSpy);

        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                StyleService,
                { provide: RendererFactory2, useValue: rendererFactorySpy }
            ]
        });
        service = TestBed.inject(StyleService);
        httpMock = TestBed.inject(HttpTestingController);
        renderer = rendererSpy;
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should fetch aggregated CSS and apply it to a style element', () => {
        const mockCss = ':root { --test: #fff; }';
        const characterId = 1;
        const mockNewStyleEl = { nodeName: 'STYLE' };

        spyOn(document, 'getElementById').and.returnValue(null);
        renderer.createElement.and.returnValue(mockNewStyleEl);

        service.fetchAggregatedCss(characterId).subscribe();

        const req = httpMock.expectOne(request =>
            request.url === '/api/styles/aggregated' &&
            request.params.get('characterId') === '1'
        );
        req.flush(mockCss);

        expect(renderer.createElement).toHaveBeenCalledWith('style');
        expect(renderer.setProperty).toHaveBeenCalledWith(mockNewStyleEl, 'id', 'dynamic-character-styles');
        expect(renderer.appendChild).toHaveBeenCalledWith(document.head, mockNewStyleEl);
        expect(renderer.setProperty).toHaveBeenCalledWith(mockNewStyleEl, 'innerHTML', mockCss);
    });

    it('should update existing style element instead of creating a new one', () => {
        const mockCss = '.new { color: red; }';
        const mockStyleEl = { id: 'dynamic-character-styles' };
        spyOn(document, 'getElementById').and.returnValue(mockStyleEl as any);

        service.fetchAggregatedCss(1).subscribe();

        const req = httpMock.expectOne(r => r.url === '/api/styles/aggregated');
        req.flush(mockCss);

        expect(renderer.createElement).not.toHaveBeenCalled();
        expect(renderer.setProperty).toHaveBeenCalledWith(mockStyleEl as any, 'innerHTML', mockCss);
    });

    it('should clear dynamic styles', () => {
        const mockStyleEl = { id: 'dynamic-character-styles' };
        spyOn(document, 'getElementById').and.returnValue(mockStyleEl as any);

        service.clearDynamicStyles();

        expect(renderer.removeChild).toHaveBeenCalledWith(document.head, mockStyleEl as any);
    });

    it('should not throw when clearing and element does not exist', () => {
        spyOn(document, 'getElementById').and.returnValue(null);
        expect(() => service.clearDynamicStyles()).not.toThrow();
        expect(renderer.removeChild).not.toHaveBeenCalled();
    });
});
