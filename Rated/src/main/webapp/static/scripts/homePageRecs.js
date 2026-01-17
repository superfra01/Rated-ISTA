/**
 * Gestisce lo scroll orizzontale della lista consigliati
 * @param {number} direction - 1 per destra, -1 per sinistra
 */
function scrollRecs(direction) {
    const wrapper = document.getElementById('recWrapper');
    const scrollAmount = 300; // Quantità di pixel per ogni click
    
    if (wrapper) {
        wrapper.scrollBy({
            left: scrollAmount * direction,
            behavior: 'smooth'
        });
    }
}

/**
 * Chiama la servlet per ignorare il film e rimuove la card visivamente
 * @param {number} filmId - ID del film da ignorare
 */
function ignoraFilm(filmId) {
    // URL della Servlet NonInteressatoServlet
    const url = 'NonInteressatoServlet';
    
    // Parametri per la richiesta POST
    const params = new URLSearchParams();
    params.append('filmId', filmId);

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params
    })
    .then(response => {
        if (response.ok) {
            // Se la rimozione ha successo lato server, rimuovi elemento dal DOM
            animazioneRimozione(filmId);
        } else {
            return response.text().then(text => { throw new Error(text) });
        }
    })
    .catch(error => {
        console.error('Errore durante la rimozione del consiglio:', error);
        alert('Si è verificato un errore. Riprova più tardi.');
    });
}

/**
 * Applica un effetto fade-out e rimuove l'elemento dal DOM
 * @param {number} filmId 
 */
function animazioneRimozione(filmId) {
    const card = document.getElementById('film-card-' + filmId);
    if (card) {
        card.style.transition = 'opacity 0.5s, transform 0.5s';
        card.style.opacity = '0';
        card.style.transform = 'scale(0.8)';
        
        setTimeout(() => {
            card.remove();
            
            // Opzionale: Se non ci sono più film, nascondi l'intera sezione
            const wrapper = document.getElementById('recWrapper');
            if (wrapper && wrapper.children.length === 0) {
                const section = document.querySelector('.recommendations-section');
                if (section) section.style.display = 'none';
            }
        }, 500);
    }
}