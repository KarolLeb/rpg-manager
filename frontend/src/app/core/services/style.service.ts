import { Injectable, inject, Renderer2, RendererFactory2 } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class StyleService {
    private readonly http = inject(HttpClient);
    private readonly rendererFactory = inject(RendererFactory2);
    private readonly renderer: Renderer2;
    private readonly styleElementId = 'dynamic-character-styles';
    private readonly apiUrl = '/api/styles';

    constructor() {
        this.renderer = this.rendererFactory.createRenderer(null, null);
    }

    fetchAggregatedCss(characterId: number): Observable<string> {
        const params = new HttpParams().set('characterId', characterId.toString());

        return this.http.get(`${this.apiUrl}/aggregated`, {
            params,
            responseType: 'text'
        }).pipe(
            tap(css => this.applyCss(css))
        );
    }

    private applyCss(css: string): void {
        let styleEl = document.getElementById(this.styleElementId);
        if (!styleEl) {
            styleEl = this.renderer.createElement('style');
            this.renderer.setProperty(styleEl, 'id', this.styleElementId);
            this.renderer.appendChild(document.head, styleEl);
        }
        this.renderer.setProperty(styleEl, 'innerHTML', css);
    }

    clearDynamicStyles(): void {
        const styleEl = document.getElementById(this.styleElementId);
        if (styleEl) {
            this.renderer.removeChild(document.head, styleEl);
        }
    }
}
