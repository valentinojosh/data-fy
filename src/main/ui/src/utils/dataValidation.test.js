import {containsNullOrZero} from './dataValidation';

describe('containsNullOrZero', () => {
    const completeData = {
        artistsShort: [{id: 'artist-1'}],
        topSixGenres: {rock: 12},
        tracksShort: [{id: 'track-1'}],
        minutes: 100
    };

    test('accepts a complete Spotify data response', () => {
        expect(containsNullOrZero(completeData)).toBe(false);
    });

    test.each([
        [{...completeData, minutes: 0}],
        [{...completeData, artistsShort: []}],
        [{...completeData, tracksShort: [null]}],
        [{...completeData, topSixGenres: {}}],
        [{...completeData, artistsShort: null}]
    ])('rejects an incomplete response', (data) => {
        expect(containsNullOrZero(data)).toBe(true);
    });
});
