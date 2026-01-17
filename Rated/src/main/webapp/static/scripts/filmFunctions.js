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

function showReviewForm() {
    // Mostra l'overlay
    document.getElementById('reviewOverlay').style.display = 'flex';

    // Disabilita il bottone "RATE IT" dopo il primo click
    // (se hai messo l'ID "btnRateFilm" nel JSP)
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
