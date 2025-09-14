<script>

    let currentPage = $state("home"); // "/home" | "/user/create"

    // --- user creation state ---
    let name = $state("")
    let email = $state("")
    let user = $state({
        userId: null,
        username: "",
        email: ""
    })

    // --- poll creation state ---
    let question = $state("")
    let options  = $state([""]) // start with one empty option row
    let poll     = $state(null)
    let pollError = $state("")
    let pollLoading = $state(false)


    // ---- Voting state ----
    let currentPollId = $state(null);
    let pollData      = $state(null);   // { id, question, options:[{id,text,votes}] }
    let pollLoad      = $state(false);
    let voteSending   = $state(false);
    let voteError     = $state("");

    //------- what i voted on here ------
    let myVotes = $state({});          // whole map for current user
    let myChoice = $state(null);       // optionId I chose for current poll (if any)

    //----- store Votes locally -----
    const votesKeyFor = (userId) => `pollVotes:user:${userId}`;

    function loadUserVotes(userId) {
        try {
            const json = localStorage.getItem(votesKeyFor(userId));
            return json ? JSON.parse(json) : {}; // { [pollId]: optionId }
        } catch { return {}; }
    }

    function saveUserVotes(userId, map) {
        try { localStorage.setItem(votesKeyFor(userId), JSON.stringify(map)); } catch {}
    }

    $effect(() => {
        if (user?.userId) {
            myVotes = loadUserVotes(user.userId);
        }
    });

    async function createUser() {
        //console.log("hello")
        const response = await fetch("http://localhost:8080/user/create", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                "username": name,
                "email": email
            })
        })
        user=await response.json()
    }



    function addOption()    { options = [...options, ""]; }
    function removeOption(i){ options = options.filter((_, idx) => idx !== i); }

    async function createPoll() {
        pollError = "";
        // validate
        const opts = options.map(o => o.trim()).filter(Boolean);
        if (!question.trim()) { pollError = "Please enter a question."; return; }
        if (opts.length < 2)  { pollError = "Enter at least two options."; return; }
        if (!user?.userId)    { pollError = "No creator userId — create a user first."; return; }

        pollLoading = true;
        try {
            // Choose the endpoint shape your backend uses:
            // A) body includes creatorId:
            const res = await fetch("http://localhost:8080/poll/create", { //`http://localhost:8080/users/${user.userId}/poll/create`
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    question,
                    options: opts,          // ["Yes", "No", ...]
                    creatorId: user.userId  // link to creator
                })
            });

            if (!res.ok) {
                const txt = await res.text().catch(() => "");
                throw new Error(`HTTP ${res.status} ${res.statusText} ${txt}`);
            }
            poll = await res.json();
            currentPollId = poll.id;
            currentPage = "vote";
            loadPoll(currentPollId);

            // clear form
            question = "";
            options = [""];
            // optionally navigate somewhere, or stay and show result
            // currentPage = "home";
        } catch (e) {
            pollError = e.message ?? "Failed to create poll.";
        } finally {
            pollLoading = false;
        }
    }

    // load a poll for voting
    async function loadPoll(id) {
        const n = Number(id);
        if (!Number.isInteger(n) || n <= 0) {
            voteError = "Please enter a valid poll id.";
            return;
        }
        currentPollId = n;

        pollLoad = true;
        voteError = "";
        try {
            const res = await fetch(`http://localhost:8080/poll/${n}`);
            if (!res.ok) {
                const txt = await res.text().catch(() => "");
                throw new Error(`HTTP ${res.status} ${txt}`);
            }

            const text = await res.text();
            if (!text) throw new Error("Poll not found or empty response.");
            pollData = JSON.parse(text);

            myChoice = myVotes[pollData.id] ?? null;

        } catch (e) {
            voteError = e.message || "Failed to load poll.";
            console.error(e);
            pollData = null;
        } finally {
            pollLoad = false;
        }
    }



    // cast a vote (adjust URL/body to match your backend)
    async function vote(optionId, direction = "up") {
        try {
            if (!user?.userId) {
                voteError = "Create a user (or log in) before voting.";
                return;
            }
            // optimistic UI update
            const idx = pollData.options.findIndex(o => o.id === optionId);
            if (idx !== -1) {
                const delta = direction === "up" ? 1 : -1;
                pollData = {
                    ...pollData,
                    options: pollData.options.map(o =>
                        o.id === optionId ? { ...o, votes: (o.votes ?? 0) + delta } : o
                    )
                };
            }

            const res = await fetch(`http://localhost:8080/poll/${pollData.id}/vote`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ optionId, direction, userId: user.userId }) // include userId if your API needs it
            });

            if (!res.ok) throw new Error(`HTTP ${res.status}`);

            // optionally sync with server response if backend returns updated poll
            pollData = await res.json();

            myChoice = optionId;
            myVotes = { ...myVotes, [pollData.id]: optionId };
            saveUserVotes(user.userId, myVotes);

        } catch (e) {
            alert(e.message || "Vote failed");
            // reload from server if we had a local optimistic update that might be wrong
            if (pollData && pollData.id) loadPoll(pollData.id);
        }
    }

    async function changeVote(newOptionId) {
        // simplest: just call vote again with the new option
        await vote(newOptionId, "up");
    }

    function goCreate() { currentPage = "createUser"; }
    function goHome()   { currentPage = "home"; }

    // navigate helpers you already have
    const goVote = (id) => { currentPage = "vote"; if (id) loadPoll(id); };
</script>

<main>
    {#if currentPage === "home"}
        <h2>Welcome Home!</h2>
        <p>Create a user to get started.</p>
        <button class="blue-button" onclick={goCreate}>
            Go to Create User
        </button>
        <button class="green-button" onclick={() => currentPage = "vote"} style="margin-left:10px;">
            Vote on Polls
        </button>

    {:else if currentPage === "createUser"}
        <h2>Create User</h2>

        <form onsubmit={(e) => { e.preventDefault(); createUser(); }}>
            <label>Username:</label>
            <input name="username" type="text" bind:value={name} />

            <label>Email:</label>
            <input name="email" type="text" bind:value={email} />

            <!-- This button will now submit the form -->
            <button class="red-button" type="submit">CREATE</button>
        </form>

        <pre>{JSON.stringify(user)}</pre> <!--pee-->

        {#if user?.userId}
            <button class="green-button" onclick={() => currentPage = "createPoll"}>
                ➕ Create Poll
            </button>
            <button class="green-button" onclick={() => currentPage = "vote"} style="margin-left:10px;">
                Vote on Polls
            </button>
        {/if}

        <button class="blue-button" onclick={goHome}>
            ← Back to Home
        </button>
    {:else if currentPage === "createPoll"}
        <h2>Create Poll</h2>

        <form onsubmit={(e) => { e.preventDefault(); createPoll(); }}>
            <label>Question:</label>
            <input type="text" bind:value={question} placeholder="e.g. Pineapple on pizza?" />

            <div style="grid-column: 1 / -1; margin-top: 8px;"><strong>Options</strong></div>

            {#each options as opt, i}
                <label>Option {i + 1}:</label>
                <div style="display:flex; gap:8px;">
                    <input type="text" bind:value={options[i]} placeholder={`Option #${i + 1}`} style="flex:1;" />
                    {#if options.length > 1}
                        <button type="button" class="btn-muted" onclick={() => removeOption(i)}>remove</button>
                    {/if}
                </div>
            {/each}

            <button type="button" class="btn-muted" onclick={addOption} style="grid-column:1/-1; margin-top:6px;">
                + Add option
            </button>

            {#if pollError}<p style="color:#dc2626; grid-column:1/-1;">{pollError}</p>{/if}

            <button class="red-button" type="submit" disabled={pollLoading} style="margin-top:8px;">
                {pollLoading ? "Creating…" : "Create Poll"}
            </button>
        </form>

        {#if poll}
            <pre>{JSON.stringify(poll, null, 2)}</pre>
        {/if}
        {#if pollData}
            <div class="panel">
                <div class="question"><em>"{pollData.question}"</em></div>
                {#each pollData.options as opt}
                    <div class="option">
                        <div>{opt.text}</div>
                        <button class="btn-up" onclick={() => vote(opt.id, "up")}>upvote</button>
                        <button class="btn-down" onclick={() => vote(opt.id, "down")}>downvote</button>
                        <div class="counter">{opt.votes ?? 0} Votes</div>
                    </div>
                {/each}
            </div>
        {/if}

    {:else if currentPage === "vote"}
        <h2>Vote on a Poll</h2>

        <!-- Load any poll by ID -->
        <div class="panel" style="margin-bottom:14px;">
            <label>Poll ID:</label>
            <div style="display:flex; gap:8px;">
                <input type="number" bind:value={currentPollId} min="1" />
                <button class="btn-muted" onclick={() => loadPoll(currentPollId)}>
                    Load
                </button>
            </div>
        </div>

        {#if pollLoad}
            <p>Loading poll…</p>
        {:else if voteError}
            <p class="err">{voteError}</p>
        {:else if pollData}
            <div class="panel">http://localhost:8080/poll/${pollData.id}/vote
                <div class="question"><em>"{pollData.question}"</em></div>

                {#each pollData.options as opt}
                    <div class="option" class:selected={myChoice === opt.id}>
                        <div>{opt.text}</div>

                        <!-- Block another vote if I already chose a different option -->
                        <button class="btn-up"
                                disabled={voteSending || (!!myChoice && myChoice !== opt.id)}
                                onclick={() => vote(opt.id, "up")}>
                            {myChoice === opt.id ? "voted" : "vote"}
                        </button>

                        {#if myChoice && myChoice !== opt.id}
                            <button class="btn-muted"
                                    disabled={voteSending}
                                    onclick={() => changeVote(opt.id)}>
                                change to this
                            </button>
                        {/if}

                        <div class="counter">{opt.votes ?? 0} {(opt.votes ?? 0) === 1 ? 'Vote' : 'Votes'}</div>
                    </div>
                {/each}

            </div>
        {/if}

        <button class="blue-button outline" onclick={() => currentPage = "home"}>← Back to Home</button>
    {/if}






</main>


<!--
<style>
    .red-button {
        background-color:red;
    }
</style>
-->

<style>
    main { max-width: 700px; margin: 40px auto; font-family: sans-serif; }
    .panel {
        background: #fff;
        border-radius: 12px;
        padding: 24px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    }
    form {
        display: grid;
        grid-template-columns: 100px 1fr;
        gap: 8px 12px;
        margin-bottom: 16px;
    }
    .red-button {
        grid-column: 1 / -1;
        background-color: red;
        color: white;
        border: none;
        border-radius: 8px;
        padding: 8px 14px;
    }
    .blue-button {
        background: #2563eb;
        color: white;
        border: none;
        border-radius: 8px;
        padding: 8px 14px;
        margin-top: 10px;
    }
    .green-button {
        background: #16a34a;
        color: white;
        border: none;
        border-radius: 8px;
        padding: 8px 14px;
        margin-top: 10px;
        cursor: pointer;
    }

    .btn-muted {
        background: #e5e7eb; border: 0; border-radius: 8px; padding: 8px 12px; cursor: pointer;
    }
    .green-button {
        background: #16a34a; color: #fff; border: 0; border-radius: 8px; padding: 8px 14px; margin-top: 10px;
    }
    .panel { background:#fff; border-radius:12px; padding:16px; box-shadow:0 8px 24px rgba(0,0,0,.08); }
    .question { background:#fde68a66; color:#7c4a03; border-radius:10px; padding:10px 16px; margin:10px 0 16px; }
    .option { display:grid; grid-template-columns:1fr auto auto auto; gap:10px; align-items:center;
        border:1px solid #e5e7eb; border-radius:10px; padding:10px 12px; margin-bottom:8px; background:#fff; }
    .btn-up   { background:#16a34a; color:#fff; border:0; border-radius:8px; padding:8px 10px; cursor:pointer; }
    .btn-down { background:#dc2626; color:#fff; border:0; border-radius:8px; padding:8px 10px; cursor:pointer; }
    .btn-muted{ background:#e5e7eb; color:#374151; border:0; border-radius:8px; padding:8px 12px; cursor:pointer; }
    .counter  { color:#2563eb; font-weight:700; }
    .err { color:#dc2626; }
    .blue-button.outline { background:#fff; color:#2563eb; border:2px solid #2563eb; }
    .option.selected { outline: 2px solid #2563eb; }
</style>
