/**
 * Gestisce il cambio tra la lista dei film visti e la watchlist.
 * @param {string} type - 'watched' oppure 'watchlist'
 */
function switchList(type) {
    // Ottiene l'URL corrente senza parametri
    const baseUrl = window.location.href.split('?')[0];
    // Ricarica la pagina con il nuovo parametro
    window.location.href = baseUrl + "?listType=" + type;
}