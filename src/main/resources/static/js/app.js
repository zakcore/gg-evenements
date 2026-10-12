/* GG'Événements — petites interactions (aucune dépendance). */
(function () {
    'use strict';

    // Menu mobile
    var nav = document.querySelector('.nav');
    var toggle = document.querySelector('.nav__toggle');
    if (nav && toggle) {
        toggle.addEventListener('click', function () {
            var ouvert = nav.classList.toggle('is-open');
            toggle.setAttribute('aria-expanded', String(ouvert));
            toggle.innerHTML = ouvert ? '<i class="ph-bold ph-x"></i>' : '<i class="ph-bold ph-list"></i>';
        });
    }

    // Confirmation avant les actions sensibles : <form data-confirm="Texte" data-confirm-title="..." data-confirm-ok="...">
    var dialog = document.getElementById('confirm-dialog');
    var formEnAttente = null;
    if (dialog) {
        document.addEventListener('submit', function (e) {
            var form = e.target;
            if (!form.hasAttribute('data-confirm') || form.dataset.confirmed === 'true') {
                return;
            }
            e.preventDefault();
            formEnAttente = form;
            dialog.querySelector('[data-slot="title"]').textContent = form.dataset.confirmTitle || 'Confirmer cette action ?';
            dialog.querySelector('[data-slot="text"]').textContent = form.dataset.confirm;
            var ok = dialog.querySelector('[data-slot="ok"]');
            ok.textContent = form.dataset.confirmOk || 'Confirmer';
            ok.className = 'btn ' + (form.dataset.confirmStyle === 'primary' ? 'btn--primary' : 'btn--danger');
            dialog.querySelector('.modal__icon').style.display = form.dataset.confirmStyle === 'primary' ? 'none' : '';
            dialog.showModal();
            dialog.querySelector('[data-slot="cancel"]').focus();
        });
        dialog.addEventListener('close', function () {
            if (dialog.returnValue === 'ok' && formEnAttente) {
                formEnAttente.dataset.confirmed = 'true';
                formEnAttente.submit();
            }
            formEnAttente = null;
            dialog.returnValue = '';
        });
        dialog.addEventListener('click', function (e) {
            if (e.target === dialog) { dialog.close('cancel'); }
        });
    }

    // Listes déroulantes qui relancent la recherche immédiatement
    document.querySelectorAll('[data-autosubmit]').forEach(function (champ) {
        champ.addEventListener('change', function () {
            var page = champ.form.querySelector('input[name="page"]');
            if (page) { page.value = 0; }
            champ.form.submit();
        });
    });

    // Fermeture des messages
    document.querySelectorAll('.flash__close').forEach(function (bouton) {
        bouton.addEventListener('click', function () { bouton.closest('.flash').remove(); });
    });

    // Compteur de caractères : <textarea data-counter="500">
    document.querySelectorAll('[data-counter]').forEach(function (zone) {
        var max = parseInt(zone.dataset.counter, 10);
        var sortie = document.querySelector('[data-counter-for="' + zone.id + '"]');
        if (!sortie) { return; }
        var maj = function () {
            sortie.textContent = zone.value.length + ' / ' + max;
            sortie.style.color = zone.value.length > max ? 'var(--red)' : '';
        };
        zone.addEventListener('input', maj);
        maj();
    });

    // Aperçu de l'affiche choisie
    document.querySelectorAll('[data-preview]').forEach(function (champ) {
        var img = document.getElementById(champ.dataset.preview);
        champ.addEventListener('change', function () {
            var fichier = champ.files && champ.files[0];
            if (img && fichier && fichier.type.indexOf('image/') === 0) {
                img.src = URL.createObjectURL(fichier);
            }
        });
    });
})();
