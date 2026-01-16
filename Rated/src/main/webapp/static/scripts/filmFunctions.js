function voteReview(idFilm, emailRecensore, valutazione) {
    const formData = new URLSearchParams();
    formData.append("idFilm", idFilm);
    formData.append("emailRecensore", emailRecensore);
    formData.append("valutazione", valutazione);

    fetch("VoteReview", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: formData.toString()
    })
        .then(response => {
            if (response.ok) {
                window.location.reload();
            } else {
                alert("Errore durante la votazione. Riprova più tardi.");
            }
        })
        .catch(error => {
            console.error("Errore nella richiesta:", error);
            alert("Errore durante la votazione. Riprova più tardi.");
        });
}

// --- FUNZIONE GESTIONE LISTE AGGIORNATA ---
function toggleUserList(idFilm, listType, buttonElement) {
    // Determiniamo la servlet corretta in base al tipo di lista
    let urlServlet = "";
    if (listType === 'watchlist') {
        urlServlet = "AggiungiWatchlistServlet";
    } else if (listType === 'watched') {
        urlServlet = "SegnaComeVistoServlet";
    }

    const formData = new URLSearchParams();
    // *** MODIFICA EFFETTUATA QUI ***
    // Prima era "idFilm", ora è "filmId" per coincidere con request.getParameter("filmId") nelle Servlet
    formData.append("filmId", idFilm); 

    // Chiamata al backend usando la servlet specifica
    fetch(urlServlet, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: formData.toString()
    })
    .then(response => {
        if (response.ok) {
            // Toggle visuale della classe 'active'
            buttonElement.classList.toggle("active");
            
            // --- LOGICA AGGIORNAMENTO UI E MUTUA ESCLUSIONE ---
            const btnWatchlist = document.querySelector(".btn-watchlist");
            const btnWatched = document.querySelector(".btn-watched");

            // Aggiornamento testi/icone del bottone cliccato
            if (buttonElement.classList.contains("active")) {
                if(listType === 'watchlist') {
                    buttonElement.innerHTML = '<i class="fas fa-check"></i> In Watchlist';
                    // Se aggiungo alla Watchlist, rimuovo da Watched (se presente)
                    if(btnWatched && btnWatched.classList.contains("active")) {
                         btnWatched.classList.remove("active");
                         btnWatched.innerHTML = '<i class="fas fa-check-circle"></i> Segna come Visto';
                    }
                } else {
                    // List Type == Watched
                    buttonElement.innerHTML = '<i class="fas fa-check-double"></i> Visto';
                    // Se aggiungo a Visto, rimuovo da Watchlist (se presente)
                    if(btnWatchlist && btnWatchlist.classList.contains("active")) {
                         btnWatchlist.classList.remove("active");
                         btnWatchlist.innerHTML = '<i class="fas fa-bookmark"></i> Aggiungi alla Watchlist';
                    }
                }
            } else {
                 // Caso disattivazione
                 if(listType === 'watchlist') {
                    buttonElement.innerHTML = '<i class="fas fa-bookmark"></i> Aggiungi alla Watchlist';
                } else {
                    buttonElement.innerHTML = '<i class="fas fa-check-circle"></i> Segna come Visto';
                }
            }
        } else {
            alert("Impossibile aggiornare la lista. Riprova.");
        }
    })
    .catch(error => {
        console.error("Errore chiamata liste:", error);
        alert("Errore di connessione.");
    });
}

function showReviewForm() {
    // Mostra l'overlay
    document.getElementById('reviewOverlay').style.display = 'flex';

    // Disabilita il bottone "RATE IT" dopo il primo click
    const rateButton = document.getElementById('btnRateFilm');
    if (rateButton) {
        rateButton.disabled = true;
    }
}

function hideReviewForm() {
    document.getElementById('reviewOverlay').style.display = 'none';
}

function showModifyForm() {
    document.getElementById('modifyOverlay').style.display = 'flex';
}

function hideModifyForm() {
    document.getElementById('modifyOverlay').style.display = 'none';
}

function validateReviewForm() {
    const titolo = document.getElementById('titolo').value.trim();
    const recensione = document.getElementById('recensione').value.trim();
    const valutazione = document.querySelector('input[name="valutazione"]:checked');

    if (titolo === "" || recensione === "" || !valutazione) {
        alert("Per favore, completa tutti i campi.");
        return false;
    }
    return true;
}

function deleteFilm(idFilm) {
    if (confirm("Sei sicuro di voler eliminare questo film? Questa azione non può essere annullata.")) {
        // Crea un form temporaneo
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = 'deleteFilm';

        // Aggiungi l'input nascosto
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'idFilm';
        input.value = idFilm;
        form.appendChild(input);

        // Aggiungi il form al body e sottometti
        document.body.appendChild(form);
        form.submit();
    }
}

function reportReview(idFilm, emailRecensore) {
    if (confirm("Sei sicuro di voler segnalare questa recensione?")) {
        const formData = new URLSearchParams();
        formData.append("idFilm", idFilm);
        formData.append("reviewerEmail", emailRecensore);

        fetch("ReportReview", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: formData.toString()
        })
            .then(response => {
                if (response.ok) {
                    alert("Recensione segnalata con successo.");
                } else {
                    alert("Errore durante la segnalazione. Riprova più tardi.");
                }
            })
            .catch(error => {
                console.error("Errore nella richiesta:", error);
                alert("Errore durante la segnalazione. Riprova più tardi.");
            });
    }
}